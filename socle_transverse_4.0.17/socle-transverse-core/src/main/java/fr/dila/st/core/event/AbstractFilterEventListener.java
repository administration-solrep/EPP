package fr.dila.st.core.event;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

/**
 * can check event name and/or context type
 *
 * @author SPL
 *
 * @param <T>
 */
public abstract class AbstractFilterEventListener<T> extends AbstractEventListener {
    private static final Log LOG = LogFactory.getLog(AbstractFilterEventListener.class);

    private final Set<String> eventNames;
    private final Class<? extends EventContext> contextClazz;

    protected AbstractFilterEventListener(String eventName, Class<? extends EventContext> contextClazz) {
        super();
        this.eventNames = new HashSet<String>();
        if (eventName != null) {
            this.eventNames.add(eventName);
        }
        this.contextClazz = contextClazz;
    }

    protected AbstractFilterEventListener(String[] eventNames, Class<? extends EventContext> contextClazz) {
        super();
        this.eventNames = new HashSet<String>();
        if (eventNames != null) {
            for (String name : eventNames) {
                this.eventNames.add(name);
            }
        }
        this.contextClazz = contextClazz;
    }

    protected AbstractFilterEventListener(String eventName) {
        this(eventName, null);
    }

    protected AbstractFilterEventListener(String[] eventNames) {
        this(eventNames, null);
    }

    protected AbstractFilterEventListener(Class<? extends EventContext> contextClazz) {
        super();
        this.eventNames = new HashSet<String>();
        this.contextClazz = contextClazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doHandleEvent(Event event) {
        if (!eventNames.isEmpty() && !eventNames.contains(event.getName())) {
            LOG.warn("listener called on incorrect event (" + event.getName() + "instead of " + eventNames + ")");
            return;
        }
        if (contextClazz != null && !contextClazz.isAssignableFrom(event.getContext().getClass())) {
            LOG.warn(
                "listener called on incorrect context (" +
                event.getContext().getClass() +
                "instead of " +
                contextClazz +
                ")"
            );
            return;
        }
        doHandleEvent(event, (T) event.getContext());
    }

    protected abstract void doHandleEvent(Event event, T context);
}
