package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;

/**
 * On doit récupérer tous les dossiers dont les feuilles de route sont bloquées
 * cad sont toujours en exécution mais dont aucune étape n'est en cours
 * et envoyer un mail aux administrateurs fonctionnels afin de les prevenir des dossiers bloqués
 *
 */
public abstract class AbstractBlockedRouteAlertBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractBlockedRouteAlertBatchListener.class);

    protected AbstractBlockedRouteAlertBatchListener() {
        super(LOGGER, SSEventConstant.BLOCKED_ROUTES_ALERT_EVENT);
    }

    @Override
    public void processEvent(final CoreSession session, final Event event) {
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_DOSS_BLOQUES_TEC, "Début");
        batchType = TypeBatch.FONCTIONNEL;
        long startTime = Calendar.getInstance().getTimeInMillis();
        int nbDossiersBloques = 0;
        try {
            final List<String> idsFailedDossiersList = new ArrayList<String>(searchForFailed(session));
            nbDossiersBloques = idsFailedDossiersList.size();
            if (nbDossiersBloques == 0) {
                LOGGER.info(
                    session,
                    SSLogEnumImpl.PROCESS_B_DOSS_BLOQUES_TEC,
                    "Aucun dossier bloqué. Aucun envoi de mail."
                );
            } else {
                LOGGER.info(session, STLogEnumImpl.SEND_MAIL_TEC);
                sendMailAlertDossierFailed(session, idsFailedDossiersList);
            }
        } catch (final NuxeoException ce) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_PROCESS_BATCH_TEC,
                "Erreur lors de la récupération des dossiers bloqués",
                ce
            );
            ++errorCount;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre de dossiers trouvés",
                nbDossiersBloques,
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_DOSS_BLOQUES_TEC, "Fin");
    }

    /**
     * Méthode cherchant les dossiers en cours, récupérant leur feuille de route, et vérifiant si elle a ou pas une étape en cours Si aucune étape en cours n'est trouvée, on considère le dossier bloqué.
     *
     * @param session
     * @return Liste d'id des dossiers bloqués
     */
    protected Set<String> searchForFailed(final CoreSession session) {
        final Set<String> idsFailedDossiersList = new HashSet<String>();

        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { FlexibleQueryMaker.COL_ID },
                getQuery(),
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
        return idsFailedDossiersList;
    }

    /**
     * Méthode pour envoyer un mail aux administrateurs fonctionnels contenant une pièce jointe excel avec les données des dossiers bloqués.
     *
     * @param session
     * @param idsDossiers
     * @throws ClientException
     */
    protected void sendMailAlertDossierFailed(final CoreSession session, final List<String> idsDossiers) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        String content = paramService.getParametreValue(
            session,
            STParametreConstant.TEXTE_MAIL_ALERTE_DOSSIERS_BLOQUES
        );
        final String object = paramService.getParametreValue(
            session,
            STParametreConstant.OBJET_MAIL_ALERTE_DOSSIERS_BLOQUES
        );
        final String nomFichier = "Resultat_dossier_bloques_alerte.xls";
        final ProfileService profileService = STServiceLocator.getProfileService();
        final List<STUser> users = profileService.getUsersFromBaseFunction(
            STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER
        );
        final List<String> addresses = new ArrayList<String>();

        // Récuperation des adresses e-mail des administrateurs fonctionnels
        for (final STUser user : users) {
            final String mail = user.getEmail();
            if (StringUtils.isNotEmpty(mail)) {
                addresses.add(mail);
            }
        }

        // On change la variable nb_resultats pour afficher le nombre dans le mail
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nb_resultats", String.valueOf(idsDossiers.size()));
        content = StringHelper.renderFreemarker(content, paramMap);

        // Création du fichier excel qui sera joint au mail
        final DataSource fichierResultat = generateData(session, idsDossiers);

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
    protected abstract DataSource generateData(CoreSession session, List<String> idsDossiersDoc);

    /**
     * Requête Sql de récupération des ids de dossiers bloqués
     * @return
     */
    protected abstract String getQuery();
}
