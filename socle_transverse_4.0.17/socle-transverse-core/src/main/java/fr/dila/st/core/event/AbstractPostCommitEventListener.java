package fr.dila.st.core.event;

import static fr.dila.st.core.util.CoreSessionUtil.getRepo;

import java.util.stream.StreamSupport;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

public abstract class AbstractPostCommitEventListener implements PostCommitEventListener {

    @Override
    public void handleEvent(EventBundle events) {
        StreamSupport.stream(events.spliterator(), false).filter(this::accept).forEach(this::handleEvent);
    }

    private void handleEvent(Event event) {
        CoreInstance.doPrivileged(
            getRepo(),
            session -> {
                handleEvent(event, session);
            }
        );
    }

    protected abstract void handleEvent(Event event, CoreSession session);

    protected abstract boolean accept(Event event);
}
