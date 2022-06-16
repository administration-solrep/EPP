package fr.dila.st.core.event;

import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;

public abstract class AbstractSyncEventListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        if (accept(event)) {
            doHandleEvent(event);
        }
    }

    protected abstract void doHandleEvent(Event event);

    protected abstract boolean accept(Event event);
}
