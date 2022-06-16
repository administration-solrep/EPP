package fr.dila.st.core.audit;

import static org.nuxeo.ecm.core.schema.FacetNames.SYSTEM_DOCUMENT;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_COMMENT;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.DeletedDocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.audit.api.ExtendedInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.service.DefaultAuditBackend;
import org.nuxeo.ecm.platform.audit.service.NXAuditEventsService;
import org.nuxeo.ecm.platform.audit.service.extension.AuditBackendDescriptor;

/**
 * Surcharge de l'audit backend par défaut pour gérer le username et la taille des commentaires
 *
 */
public class STAuditBackend extends DefaultAuditBackend {
    private static final Logger LOG = LogManager.getLogger(STAuditBackend.class);

    public static final int MAX_SIZE_COMMENT = 1024;

    public STAuditBackend() {
        super();
    }

    public STAuditBackend(NXAuditEventsService component, AuditBackendDescriptor config) {
        super(component, config);
    }

    @Override
    public LogEntry buildEntryFromEvent(Event event) {
        EventContext ctx = event.getContext();
        String eventName = event.getName();
        Date eventDate = new Date(event.getTime());

        LogEntry entry = newLogEntry();
        entry.setEventId(eventName);
        entry.setEventDate(eventDate);

        if (ctx instanceof DocumentEventContext) {
            DocumentEventContext docCtx = (DocumentEventContext) ctx;
            DocumentModel document = docCtx.getSourceDocument();
            if (document.hasFacet(SYSTEM_DOCUMENT) && !document.hasFacet(FORCE_AUDIT_FACET)) {
                // do not log event on System documents
                // unless it has the FORCE_AUDIT_FACET facet
                return null;
            }

            Boolean disabled = (Boolean) docCtx.getProperty(NXAuditEventsService.DISABLE_AUDIT_LOGGER);
            if (disabled != null && disabled.booleanValue()) {
                // don't log events with this flag
                return null;
            }
            NuxeoPrincipal principal = docCtx.getPrincipal();
            Map<String, Serializable> properties = docCtx.getProperties();

            entry.setDocUUID(getDocId(document, docCtx));
            entry.setDocPath(document.getPathAsString());
            entry.setDocType(document.getType());
            entry.setRepositoryId(document.getRepositoryName());

            setUserNameInEntry(principal, entry, eventName);
            entry.setComment(trimToMaxSize((String) properties.get(LOG_COMMENT)));

            if (document instanceof DeletedDocumentModel) {
                entry.setComment("Document does not exist anymore!");
            } else {
                if (document.isLifeCycleLoaded()) {
                    entry.setDocLifeCycle(document.getCurrentLifeCycleState());
                }
            }
            if (LifeCycleConstants.TRANSITION_EVENT.equals(eventName)) {
                entry.setDocLifeCycle((String) docCtx.getProperty(LifeCycleConstants.TRANSTION_EVENT_OPTION_TO));
            }
            String category = StringUtils.defaultString(
                (String) properties.get(STEventConstant.CATEGORY_PROPERTY),
                "eventDocumentCategory"
            );
            entry.setCategory(category);

            doPutExtendedInfos(entry, docCtx, document, principal);
        } else {
            NuxeoPrincipal principal = ctx.getPrincipal();
            Map<String, Serializable> properties = ctx.getProperties();

            setUserNameInEntry(principal, entry, eventName);
            if (ctx.hasProperty(STEventConstant.DOC_REF_PROPERTY)) {
                entry.setDocUUID((String) ctx.getProperty(STEventConstant.DOC_REF_PROPERTY));
            }

            entry.setComment(trimToMaxSize((String) properties.get(LOG_COMMENT)));

            String category = (String) properties.get(STEventConstant.CATEGORY_PROPERTY);
            entry.setCategory(category);

            doPutExtendedInfos(entry, ctx, null, principal);
        }

        return entry;
    }

    /*
     * Surcharge : On récupère l'utilisateur et on le stocke en dur dans le journal.
     */
    private void setUserNameInEntry(NuxeoPrincipal principal, LogEntry entry, String eventName) {
        if (principal != null && StringUtils.isNotEmpty(principal.getActingUser())) {
            String actingUser = principal.getActingUser();

            Map<String, ExtendedInfo> infos = new HashMap<>();
            ExtendedInfo info = newExtendedInfo(actingUser);
            infos.put("login", info);
            entry.setExtendedInfos(infos);

            if (actingUser.equals(STConstant.NUXEO_SYSTEM_USERNAME)) {
                // cas où l'opération a été lancé par le système : on ne cherche pas les postes de l'utilisateur
                entry.setPrincipalName(STConstant.SYSTEM_USERNAME);
                entry.setDocPath("");
            } else {
                try {
                    STUserService sTUserService = STServiceLocator.getSTUserService();

                    // récupération des postes de l'utilisateur
                    String postesUser = sTUserService.getUserProfils(actingUser);
                    // WARN : on stocke les profils de l'utilisateur dans la variable 'DocPath' que l'on utilisais pas
                    // auparavant et qui peut contenir jusqu'à 1024 caractères .
                    entry.setDocPath(trimToMaxSize(postesUser));
                    // récupération du nom complet de l'utilisateur
                    actingUser = sTUserService.getLegacyUserFullName(actingUser);
                } catch (Exception exc) {
                    entry.setDocPath("");
                    LOG.warn(exc.getMessage(), exc);
                }
                entry.setPrincipalName(actingUser);
            }
        } else {
            LOG.warn("received event {} with null principal", eventName);
        }
    }

    private String getDocId(DocumentModel doc, DocumentEventContext docCtx) {
        return docCtx.hasProperty(STEventConstant.DOC_REF_PROPERTY)
            ? (String) docCtx.getProperty(STEventConstant.DOC_REF_PROPERTY)
            : doc.getId();
    }

    /*
     * Surcharge : on limite la taille du commentaire à 1024 caractères.
     */
    private String trimToMaxSize(String str) {
        return StringUtils.isNotEmpty(str) && str.length() > MAX_SIZE_COMMENT
            ? str.substring(0, MAX_SIZE_COMMENT - 1)
            : str;
    }
}
