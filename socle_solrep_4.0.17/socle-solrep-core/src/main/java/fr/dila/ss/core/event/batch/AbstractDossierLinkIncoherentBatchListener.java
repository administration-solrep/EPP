package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;

/**
 * Détection d'incohérence entre les dossiers link et les étapes de feuille de route associée
 *
 */
public abstract class AbstractDossierLinkIncoherentBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractDossierLinkIncoherentBatchListener.class);

    protected AbstractDossierLinkIncoherentBatchListener() {
        super(LOGGER, SSEventConstant.INCOHERENT_DOSSIER_LINK_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_DL_INCOHERENT_TEC, "Début");
        long startTime = Calendar.getInstance().getTimeInMillis();
        long nbIncoherence = 0;
        try {
            List<String> dossiersIds = searchForIncoherence(session);
            if (dossiersIds.isEmpty()) {
                LOGGER.info(
                    session,
                    SSLogEnumImpl.PROCESS_B_DL_INCOHERENT_TEC,
                    "Aucune erreur detectée. Aucun envoi de mail."
                );
            } else {
                LOGGER.info(session, STLogEnumImpl.SEND_MAIL_TEC);
                sendMail(session, dossiersIds);
                nbIncoherence = dossiersIds.size();
            }
        } catch (NuxeoException ce) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_PROCESS_BATCH_TEC,
                "Erreur lors de la détection d'incohérence",
                ce
            );
            ++errorCount;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre d'incohérences trouvées",
                nbIncoherence,
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_DL_INCOHERENT_TEC, "Fin");
    }

    /**
     * Recherche les cas de dossiers link incoherents dans l'application.
     * Dossier link absent, déjà à l'état done, dossier link présent pour une étape autre qu'à running
     * @param session
     * @return List<DocumentModel> la liste des dossiers qui ont révélés une incohérence
     * @throws ClientException
     */
    protected List<String> searchForIncoherence(final CoreSession session) {
        final Set<String> idsFailedDossiersList = new HashSet<String>();

        for (String query : getQueries()) {
            try (
                IterableQueryResult res = QueryUtils.doSqlQuery(
                    session,
                    new String[] { FlexibleQueryMaker.COL_ID },
                    query,
                    new Object[] {}
                )
            ) {
                Iterator<Map<String, Serializable>> iterator = res.iterator();
                while (iterator.hasNext()) {
                    Map<String, Serializable> row = iterator.next();
                    String dossierId = (String) row.get(FlexibleQueryMaker.COL_ID);
                    idsFailedDossiersList.add(dossierId);
                }
            }
        }
        return new ArrayList<String>(idsFailedDossiersList);
    }

    protected void sendMail(final CoreSession session, final List<String> dossiersIdsList) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        String content = paramService.getParametreValue(
            session,
            STParametreConstant.TEXTE_MAIL_DOSSIERS_LINK_INCOHERENT
        );
        final String object = paramService.getParametreValue(
            session,
            STParametreConstant.OBJET_MAIL_DOSSIERS_LINK_INCOHERENT
        );
        final String nomFichier = "Resultat_dossier_link_incoherent.xls";

        // Ajout du mail de l'administrateur technique dans la liste des addresses
        final List<String> addresses = Collections.singletonList(
            paramService.getParametreValue(session, STParametreConstant.MAIL_ADMIN_TECHNIQUE)
        );

        // On change la variable nb_resultats pour afficher le nombre dans le mail
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nb_resultats", String.valueOf(dossiersIdsList.size()));
        content = StringHelper.renderFreemarker(content, paramMap);

        // Création du fichier excel qui sera joint au mail
        final DataSource fichierResultat = generateData(session, dossiersIdsList);

        final STMailService mailService = STServiceLocator.getSTMailService();
        try {
            if (fichierResultat == null) {
                // Si le fichier excel n'a pas été créé, on envoie malgré tout le mail
                mailService.sendTemplateMail(addresses, object, content, paramMap);
            } else {
                mailService.sendMailWithAttachement(addresses, object, content, nomFichier, fichierResultat);
            }
        } catch (final Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
            ++errorCount;
        }
    }

    /**
     * Méthode pour générer un fichier qui pourra être envoyé par mail. Utilisation de la classe ExcelUtil de reponses ou celle d'Epg suivant le cas
     *
     * @return DataSource si on a pu la créer, null sinon
     */
    protected abstract DataSource generateData(CoreSession session, List<String> dossiersIds);

    /**
     * Récupère la liste des requêtes à jouer. Ces requêtes doivent renvoyer un select sur une colonne id de la table des dossiers
     */
    protected abstract List<String> getQueries();
}
