package fr.dila.solonepp.core.event;

import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.core.event.AbstractLogEventListener;

/**
 * Gestionnaire d'évènements executé au login de l'utilisateur.
 *  
 * @author jtremeaux
 */
public class LoginSuccessListener extends AbstractLogEventListener {

//    private static final Log log = LogFactory.getLog(LoginSuccessListener.class);

    public LoginSuccessListener(){
    	super(LOGGING_SUCCESS_EVENT_NAME);
    }
    
    @Override
    protected void processLogin(final CoreSession session, final Set<String> principals) throws ClientException {
    	
    	// SPL : comme le listener ne fait plus rien, la conf a ete change : enabled=false
    	// pour solonEppLoginSuccessListener
    	
//            final UserManager userManager = STServiceLocator.getUserManager();
//            for(final String username : principals){
//            	final NuxeoPrincipal principal = userManager.getPrincipal(username);
//    
//                // Crée les mailbox des postes de l'utilisateur
//            	createPosteMailbox(session, principal);
//            }
    
            
    }
}
