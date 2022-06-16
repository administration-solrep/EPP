package fr.dila.st.rest.stub;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;

public class WSNotificationStub implements WSNotification {

	private static final String	FILE_BASEPATH	= "fr/dila/st/rest/stub/wsnotification/";

	@Override
	public String test() throws Exception {
		return WSNotification.SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSNotification();
	}

	@Override
	public EnvoyerNotificationResponse envoyerNotification(EnvoyerNotificationRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSnotification_envoyerNotificationResponse.xml",
				EnvoyerNotificationResponse.class);
	}

}
