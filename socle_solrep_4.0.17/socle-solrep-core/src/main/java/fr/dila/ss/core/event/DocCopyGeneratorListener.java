package fr.dila.ss.core.event;

import fr.dila.st.core.event.AbstractFilterEventListener;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;

/**
 * Ce listener permet de générer un nouveau numéro de séquence pour les documents créés par copie.
 * /!\ Ce listener fonctionne dans le cas simple, où aucun document n'est "attaché" à l'UID généré.
 * Dans le cas contraire, il faudra attacher les documents fils au nouveau numéro de séquence.
 *
 * @author jtremeaux
 */
public class DocCopyGeneratorListener extends AbstractFilterEventListener<DocumentEventContext> {
    private static final Log LOG = LogFactory.getLog(DocCopyGeneratorListener.class);

    public DocCopyGeneratorListener() {
        super(DocumentEventTypes.DOCUMENT_CREATED_BY_COPY, DocumentEventContext.class);
    }

    @Override
    protected void doHandleEvent(final Event event, final DocumentEventContext docCtx) {
        final DocumentModel doc = docCtx.getSourceDocument();
        if (doc.isProxy() || doc.isVersion()) {
            // a proxy or version keeps the uid of the document
            // being proxied or versioned => we're not allowed
            // to modify its field.
            return;
        }
        final String eventId = event.getName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("eventId : " + eventId);
        }
        try {
            addUIDtoDoc(doc);
        } catch (NuxeoException e) {
            LOG.error("Error occurred while generating UID for doc: " + doc, e);
        }
    }

    private static void addUIDtoDoc(DocumentModel doc) {
        UIDGeneratorService service = ServiceUtil.getRequiredService(UIDGeneratorService.class);
        // generate UID for our doc
        service.setUID(doc);
    }
}
