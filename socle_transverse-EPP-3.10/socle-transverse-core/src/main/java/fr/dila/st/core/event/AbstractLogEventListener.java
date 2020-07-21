package fr.dila.st.core.event;

import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.SimplePrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.core.util.SessionUtil;

/**
 * Implementation de base pour gérer les eveneement de login : logout en postcommit
 * 
 * @author SPL
 * 
 */
public abstract class AbstractLogEventListener implements PostCommitEventListener {

	public static final String	LOGGING_SUCCESS_EVENT_NAME	= "loginSuccess";
	public static final String	LOGOUT_EVENT_NAME			= "logout";

	private static final Log	LOG							= LogFactory.getLog(AbstractLogEventListener.class);

	private final String		eventName;

	public AbstractLogEventListener(final String eventName) {
		this.eventName = eventName;
	}

	@Override
	public void handleEvent(EventBundle events) throws ClientException {
		if (events.containsEventName(eventName)) {

			final Set<String> principals = new HashSet<String>();
			for (Event evt : events) {
				if (eventName.equals(evt.getName())) {
					principals.add(((SimplePrincipal) evt.getContext().getPrincipal()).getName());
				}
			}

			if (!principals.isEmpty()) {
				processNames(principals);
			}
		}
	}

	protected void processNames(final Set<String> principals) throws ClientException {
		LoginContext loginCtx = null;
		CoreSession session = null;
		try {
			try {
				loginCtx = Framework.login();
			} catch (LoginException e) {
				LOG.error("Can not connect", e);
				return;
			}
			session = SessionUtil.getCoreSession();

			processLogin(session, principals);
		} finally {
			SessionUtil.close(session);
			if (loginCtx != null) {
				try {
					loginCtx.logout();
				} catch (LoginException e) {
					LOG.error("Error while logging out", e);
				}
			}
		}
	}

	protected abstract void processLogin(final CoreSession session, final Set<String> principals)
			throws ClientException;

}
