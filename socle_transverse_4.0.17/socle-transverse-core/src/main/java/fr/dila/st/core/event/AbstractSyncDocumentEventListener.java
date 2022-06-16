package fr.dila.st.core.event;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public abstract class AbstractSyncDocumentEventListener extends AbstractSyncEventListener {

    @Override
    public void handleEvent(Event event) {
        if (accept(event)) {
            doHandleEvent(event);
        }
    }

    @Override
    protected void doHandleEvent(Event event) {
        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
        CoreSession session = docCtx.getCoreSession();
        DocumentModel doc = docCtx.getSourceDocument();
        handleEvent(event, docCtx, session, doc);
    }

    protected abstract void handleEvent(
        Event event,
        DocumentEventContext docCtx,
        CoreSession session,
        DocumentModel doc
    );

    protected boolean accept(Event event) {
        return event.getContext() instanceof DocumentEventContext;
    }
}
