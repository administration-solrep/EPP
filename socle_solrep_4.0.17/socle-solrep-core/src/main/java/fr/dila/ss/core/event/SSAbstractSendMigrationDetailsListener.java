package fr.dila.ss.core.event;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.core.util.SSExcelUtil;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

/**
 * Listener d'envoi des détails de la migration.
 *
 * @author tlombard
 */
public abstract class SSAbstractSendMigrationDetailsListener implements PostCommitEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SSAbstractSendMigrationDetailsListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        if (!events.containsEventName(SSEventConstant.SEND_MIGRATION_DETAILS_EVENT)) {
            return;
        }
        for (final Event event : events) {
            if (SSEventConstant.SEND_MIGRATION_DETAILS_EVENT.equals(event.getName())) {
                handleEvent(event);
            }
        }
    }

    private void handleEvent(Event event) {
        final EventContext eventCtx = event.getContext();
        // récupération des propriétés de l'événement
        final Map<String, Serializable> eventProperties = eventCtx.getProperties();
        @SuppressWarnings("unchecked")
        final List<MigrationDetailModel> detailDocs = (List<MigrationDetailModel>) eventProperties.get(
            SSEventConstant.SEND_MIGRATION_DETAILS_DETAILS_PROPERTY
        );
        final String recipient = (String) eventProperties.get(
            SSEventConstant.SEND_MIGRATION_DETAILS_RECIPIENT_PROPERTY
        );
        final MigrationLoggerModel migrationLogger = (MigrationLoggerModel) eventProperties.get(
            SSEventConstant.SEND_MIGRATION_DETAILS_LOGGER_PROPERTY
        );
        final Date dateDemande = new Date(event.getTime());

        DataSource excelFile = SSExcelUtil.exportMigrationDetails(eventCtx.getCoreSession(), detailDocs);
        final STMailService mailService = STServiceLocator.getSTMailService();
        final String content =
            "Bonjour, veuillez trouver en pièce jointe l'export demandé le " +
            SolonDateConverter.DATE_SLASH.format(dateDemande) +
            " des détails de la migration '" +
            getChangementGouvernementService().getLogMessage(migrationLogger) +
            "' lancée le " +
            SolonDateConverter.DATETIME_SLASH_A_MINUTE_COLON.format(migrationLogger.getStartDate()) +
            ".";
        final String object = getObjectPrefix() + " Votre demande d'export des détails d'une migration";
        final String nomFichier = "export_details_migration.xls";

        try {
            mailService.sendMailWithAttachement(
                Collections.singletonList(recipient),
                object,
                content,
                nomFichier,
                excelFile
            );
        } catch (Exception exc) {
            LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
        }
    }

    protected abstract String getObjectPrefix();

    protected abstract SSChangementGouvernementService getChangementGouvernementService();
}
