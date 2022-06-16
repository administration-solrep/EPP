package fr.dila.st.rest.client;

import fr.dila.st.rest.api.WSNotification;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;

public class WSNotificationProxy extends AbstractWsProxy implements WSNotification {

	public WSNotificationProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_NAME_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_NAME_VERSION, VersionResponse.class);
	}

	@Override
	public EnvoyerNotificationResponse envoyerNotification(EnvoyerNotificationRequest request) throws Exception {
		return doPost(METHOD_NAME_ENVOYER_NOTIFICATION, request, EnvoyerNotificationResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSNotification.SERVICE_NAME;
	}

}
