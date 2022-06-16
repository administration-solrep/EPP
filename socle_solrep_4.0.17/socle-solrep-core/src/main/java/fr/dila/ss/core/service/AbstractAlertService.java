package fr.dila.ss.core.service;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.constant.SSParametreConstant;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STAlertServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.StringHelper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.activation.DataSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.nuxeo.ecm.core.api.CoreSession;

public abstract class AbstractAlertService<T, U> extends STAlertServiceImpl {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractAlertService.class);

    private static final String EXPORT_FILENAME = "Resultat_dossier_alerte.xls";

    @Override
    public Boolean sendMail(CoreSession session, Alert alert) {
        T requeteExperte = getRequeteExperte(session, alert);
        if (requeteExperte == null) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "pas de requête trouvée pour l'alerte : " + alert.getDocument().getId()
            );
            return false;
        }

        List<String> recipients = getRecipients(alert);

        boolean isSent = false;

        try {
            STParametreService paramService = STServiceLocator.getSTParametreService();

            int nombreMaxDossiers = NumberUtils.createInteger(
                paramService.getParametreValue(session, SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME)
            );

            String username = alert.getNameCreator();

            long nbResults = countResultsFromRequete(session, requeteExperte, username);
            boolean isLimitReached = nbResults > nombreMaxDossiers;

            String subject = getEmailSubject(session, paramService, alert, isLimitReached);

            String content = getEmailContent(session, paramService, alert, isLimitReached, nbResults);

            STMailService mailService = STServiceLocator.getSTMailService();
            if (isLimitReached) {
                mailService.sendMail(recipients, subject, content);
            } else {
                //récupération des dossiers de la requête
                List<U> dossiers = getDossiersFromRequete(session, requeteExperte, username, nbResults);

                DataSource fichierExcelResultat = getDataSource(session, dossiers);

                mailService.sendMailWithAttachement(
                    recipients,
                    subject,
                    content,
                    EXPORT_FILENAME,
                    fichierExcelResultat
                );
            }

            isSent = true;
        } catch (Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "erreur lors de l'envoi de mail lors du batch d'alerte",
                e
            );
        }
        return isSent;
    }

    protected abstract T getRequeteExperte(CoreSession session, Alert alert);

    /**
     * Récupération des adresses mails des destinataires de l'alerte
     */
    protected List<String> getRecipients(Alert alert) {
        STUserService userService = STServiceLocator.getSTUserService();
        return userService.getEmailAddressFromUserList(alert.getRecipients());
    }

    protected abstract long countResultsFromRequete(CoreSession session, T requeteExperte, String username)
        throws IOException, URISyntaxException;

    private String getEmailSubject(
        CoreSession session,
        STParametreService paramService,
        Alert alert,
        boolean isLimitReached
    ) {
        String object;

        if (isLimitReached) {
            object =
                paramService.getParametreValue(session, SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME);
        } else {
            object = getDefaultEmailSubject(session, paramService);
        }

        return StringHelper.renderFreemarker(object, ImmutableMap.of("titre_alerte", alert.getTitle()));
    }

    protected abstract String getDefaultEmailSubject(CoreSession session, STParametreService paramService);

    private String getEmailContent(
        CoreSession session,
        STParametreService paramService,
        Alert alert,
        boolean isLimitReached,
        long nbResults
    ) {
        String content;

        if (isLimitReached) {
            content =
                paramService.getParametreValue(session, SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME);
        } else {
            content = getDefaultEmailContent(session, paramService, nbResults);
        }

        String login = alert.getNameCreator();
        STUserService sTUserService = STServiceLocator.getSTUserService();

        return (
            content +
            "<br/><br/>Titre de l'alerte : " +
            alert.getTitle() +
            "<br/>Créateur de l'alerte : " +
            sTUserService.getUserFullName(login) +
            "<br/>Adresse mél du créateur de l'alerte : " +
            sTUserService.getUserInfo(login, "m") +
            "<br/>Fréquence de l'alerte (en jour) : " +
            alert.getPeriodicity() +
            "<br/>Date de fin de validité de l'alerte : " +
            SolonDateConverter.DATE_SLASH.format(alert.getDateValidityEnd())
        );
    }

    protected abstract String getDefaultEmailContent(
        CoreSession session,
        STParametreService paramService,
        long nbResults
    );

    protected abstract List<U> getDossiersFromRequete(
        CoreSession session,
        T requeteExperte,
        String username,
        long nbResults
    )
        throws IOException, URISyntaxException;

    protected abstract DataSource getDataSource(CoreSession session, List<U> dossiers);
}
