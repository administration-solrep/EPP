package fr.dila.st.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;

public interface WSNotification {

	public static final String	SERVICE_NAME						= "WSnotification";

	public static final String	METHOD_NAME_TEST					= "test";
	public static final String	METHOD_NAME_VERSION					= "version";

	public static final String	METHOD_NAME_ENVOYER_NOTIFICATION	= "envoyerNotification";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public EnvoyerNotificationResponse envoyerNotification(EnvoyerNotificationRequest request) throws Exception;

}
