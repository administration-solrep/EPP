package fr.dila.st.core.override.listener;

import static org.nuxeo.ecm.core.api.LifeCycleConstants.TRANSITION_EVENT;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.ABOUT_TO_CREATE;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.BEFORE_DOC_UPDATE;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_CREATED_BY_COPY;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.SYSTEM_USERNAME;
import static org.nuxeo.ecm.core.schema.FacetNames.SYSTEM_DOCUMENT;
import static org.nuxeo.ecm.platform.dublincore.constants.DublinCoreConstants.DUBLINCORE_CREATOR_PROPERTY;
import static org.nuxeo.ecm.platform.dublincore.constants.DublinCoreConstants.DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.DateUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.SystemPrincipal;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.dublincore.service.DublinCoreStorageService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.config.ConfigurationService;

/**
 * Listener pour le dublincore en surcharge de celui de nuxeo pour éviter la
 * gestion de la propriété dc:contributors
 *
 */
public class DublinCoreListener implements EventListener {
    private static final Log LOGGER = LogFactory.getLog(DublinCoreListener.class);

    public static final String DISABLE_DUBLINCORE_LISTENER = "disableDublinCoreListener";

    private static final String RESET_CREATOR_PROPERTY = "nuxeo.dclistener.reset-creator-on-copy";

    /**
     * Default constructor
     */
    public DublinCoreListener() {
        super();
    }

    /**
     * Core event notification.
     * <p>
     * Gets core events and updates DublinCore if needed.
     *
     * @param event
     *            event fired at core layer
     */
    @Override
    public void handleEvent(final Event event) {
        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }

        Boolean block = (Boolean) event.getContext().getProperty(DISABLE_DUBLINCORE_LISTENER);
        if (Boolean.TRUE.equals(block)) {
            // ignore the event - we are blocked by the caller
            return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();

        final DocumentModel doc = docCtx.getSourceDocument();
        if (!doc.hasSchema(STSchemaConstant.DUBLINCORE_SCHEMA)) {
            return;
        }

        if (doc.isVersion()) {
            LOGGER.debug("No DublinCore update on versions except for the issued date");
            return;
        }

        if (doc.hasFacet(SYSTEM_DOCUMENT)) {
            // ignore the event for System documents
            return;
        }

        final Date eventDate = new Date(event.getTime());
        final Calendar cEventDate = DateUtil.toCalendarFromNotNullDate(eventDate);

        final String eventId = event.getName();

        DublinCoreStorageService service = Framework.getService(DublinCoreStorageService.class);

        Boolean resetCreator = (Boolean) event.getContext().getProperty(CoreEventConstants.RESET_CREATOR);
        boolean resetCreatorProperty = Framework
            .getService(ConfigurationService.class)
            .isBooleanTrue(RESET_CREATOR_PROPERTY);
        Boolean dirty = (Boolean) event.getContext().getProperty(CoreEventConstants.DOCUMENT_DIRTY);
        if (
            (eventId.equals(BEFORE_DOC_UPDATE) && Boolean.TRUE.equals(dirty)) ||
            (eventId.equals(TRANSITION_EVENT) && !doc.isImmutable())
        ) {
            service.setModificationDate(doc, cEventDate);
            addContributor(doc, event);
        } else if (eventId.equals(ABOUT_TO_CREATE)) {
            service.setCreationDate(doc, cEventDate);
            service.setModificationDate(doc, cEventDate);
            addContributor(doc, event);
        } else if (
            eventId.equals(DOCUMENT_CREATED_BY_COPY) && (resetCreatorProperty || Boolean.TRUE.equals(resetCreator))
        ) {
            Framework.doPrivileged(
                () -> {
                    doc.setPropertyValue(DUBLINCORE_CREATOR_PROPERTY, null);
                    doc.setPropertyValue(DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY, null);
                }
            );
            service.setCreationDate(doc, cEventDate);
            service.setModificationDate(doc, cEventDate);
            addContributor(doc, event);
        }
    }

    private void addContributor(DocumentModel doc, Event event) {
        NuxeoPrincipal principal = Objects.requireNonNull(event.getContext().getPrincipal());

        String principalName = principal.getActingUser();
        if (
            principal instanceof SystemPrincipal &&
            SYSTEM_USERNAME.equals(principalName) &&
            !ABOUT_TO_CREATE.equals(event.getName())
        ) {
            return;
        }

        Framework.doPrivileged(
            () -> {
                if (doc.getPropertyValue(DUBLINCORE_CREATOR_PROPERTY) == null) {
                    doc.setPropertyValue(DUBLINCORE_CREATOR_PROPERTY, principalName);
                }
                doc.setPropertyValue(DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY, principalName);
            }
        );
    }
}
