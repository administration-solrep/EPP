package fr.dila.solonepp.core.logger;

import fr.dila.st.core.logger.STNotificationAuditEventLogger;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener asynchrone post-commit qui enregistre l'Audit log pour l'application SOLON EPP.
 *
 * @author jtremeaux
 */
public class NotificationAuditEventLogger extends STNotificationAuditEventLogger {

    //    private static final Log LOGGER = LogFactory.getLog(NotificationAuditEventLogger.class);

    @Override
    protected void loggerProcess(Event event) {
        //        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
        //        DocumentModel model = docCtx.getSourceDocument();
        //        String eventName = event.getName();

        //        if (model != null) {
        //            LOGGER.debug("--------------------NotificationAuditEventLogger handleEvent() calling : " + model);

        //            String docType = model.getType();
        //            if (DocumentEventTypes.DOCUMENT_CREATED.equals(eventName) || DocumentEventTypes.DOCUMENT_UPDATED.equals(eventName)) {
        //                log de la création et modification du dossier et de la feuille de route
        //                if (SolonEppDossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
        //                    if (DocumentEventTypes.DOCUMENT_CREATED.equals(eventName)) {
        //                        docCtx.setCategory(STEventConstant.CATEGORY_BORDEREAU);
        //                        docCtx.setComment("Création du dossier");
        //                        logger.logEvent(event);
        //                    }
        //                    logDossier(event, docCtx);
        //                }
        //            }
        //        }
    }

    /**
     * Méthode utilisée pour récupérer les informations liées à un parapheur ou à un fond de Dossier.
     *
     * @param event
     * @param context
     * @return
     */
    protected Object[] getFileData(Event event, DocumentEventContext context) {
        DocumentModel model = context.getSourceDocument();
        // FileSolonEpp fichier = model.getAdapter(FileSolonEpp.class);
        Object[] datas = new Object[] {
            event.getName(),
            context.getPrincipal().getName(),
            context.getComment(),
            model.getParentRef(),
            ""
        };
        return datas;
    }
}
