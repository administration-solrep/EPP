package fr.sword.idl.naiad.nuxeo.feuilleroute.core.listener;

import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import fr.sword.naiad.nuxeo.commons.core.listener.AbstractDocumentEventListener;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 *
 */
public class CounterListener extends AbstractDocumentEventListener {
    private static int counter = 0;

    public CounterListener() {
        // do nothing
    }

    @Override
    public void handleDocumentEvent(Event event, DocumentEventContext docEventContext) {
        String category = docEventContext.getCategory();
        if (EventFirer.FEUILLE_ROUTE_CATEGORY.equals(category)) {
            counter++;
        }
    }

    public static int getCounter() {
        return counter;
    }

    public static void resetCouner() {
        counter = 0;
    }
}
