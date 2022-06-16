package fr.sword.naiad.nuxeo.commons.test.listener;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;

/**
 * Listener qui conserve le nombre d'appel sur différents evenenements
 * Permet de verifier la levée de certains evenements dans les tests unitaires
 * @author SPL
 *
 */
public class EventCallControlListener implements EventListener {

	private static final Map<String, Integer> EVENTS = new HashMap<String, Integer>();
	
	public EventCallControlListener(){
		// do nothing
	}
	
	@Override
	public void handleEvent(Event evt) throws NuxeoException {
		register(evt.getName());
	}
	
	public static void reset(){
		EVENTS.clear();
	}
	
	public static int getEventNumber(String evtName){
		Integer number = EVENTS.get(evtName);
		if(number == null){
			return 0;
		} else {
			return number.intValue();
		}
	}
	
	private static void register(String evtName){
		EVENTS.put(evtName, Integer.valueOf(getEventNumber(evtName) + 1));
	}
		
}
