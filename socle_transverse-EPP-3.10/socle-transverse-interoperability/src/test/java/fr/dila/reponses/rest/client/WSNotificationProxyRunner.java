package fr.dila.reponses.rest.client;

import java.util.Properties;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;

public class WSNotificationProxyRunner {

	private static final Logger	LOGGER					= Logger.getLogger(WSNotificationProxyRunner.class);

	// -- uncomment for Nuxeo
	// protected static final String HTTP_ENDPOINT = "http://localhost:8080";
	// protected static final String HTTP_BASEPATH = "reponses/site/reponses";
	// protected static final String USERNAME = "ws_an";
	// protected static final String PASSWORD = "ws_an";

	// --- uncomment for stub
	private static final String	HTTP_URL				= "http://server2:8180/solrep-ws-server-stub/ws/rest/";
	private static final String	HTTPS_URL				= "https://localhost:8146/solrep-ws-server-stub/ws/rest/";

	private static final String	USERNAME				= "user";
	private static final String	PASSWORD				= "secret";

	private static final String	KEYSTORE_FILE			= "src/test/resources/fr/dila/st/ws/ssl/local/keystore_client.jks";
	private static final String	KEYSTORE_PASS			= "secret";
	private static final String	KEYSTORE_FORMAT			= "JKS";

	private static final String	TRUSTSTORE_FILE			= "src/test/resources/fr/dila/st/ws/ssl/local/truststore_client.jks";
	private static final String	TRUSTSTORE_PASS			= "secret";
	private static final String	TRUSTSTORE_FORMAT		= "JKS";

	private static final String	REQUEST_BYID_FILENAME	= "fr/dila/st/rest/stub/wsnotification/WSnotification_envoyerNotificationRequest_questions_byId.xml";

	@BeforeClass
	public static void setupBeforeClass() {

		Properties systemProps = System.getProperties();

		systemProps.put("javax.net.debug", "ssl,handshake");

		systemProps.put("javax.net.ssl.keyStore", KEYSTORE_FILE);
		systemProps.put("javax.net.ssl.keyStorePassword", KEYSTORE_PASS);
		systemProps.put("javax.net.ssl.keyStoreType", KEYSTORE_FORMAT);

		systemProps.put("javax.net.ssl.trustStore", TRUSTSTORE_FILE);
		systemProps.put("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASS);
		systemProps.put("javax.net.ssl.trustStoreType", TRUSTSTORE_FORMAT);

	}

	@Test
	public void testHttp() throws Exception {
		this.run(HTTP_URL, null);
	}

	@Test
	public void testHttps() throws Exception {
		this.run(HTTPS_URL, "client2");
	}

	private void run(String url, String keyalias) throws Exception {
		LOGGER.debug("-- run");

		// --- build request
		EnvoyerNotificationRequest request = JaxBHelper.buildRequestFromFile(REQUEST_BYID_FILENAME,
				EnvoyerNotificationRequest.class);
		Assert.assertNotNull(request);

		// --- get service
		WSProxyFactory proxyFactory = new WSProxyFactory(url, null, USERNAME, PASSWORD, keyalias);
		Assert.assertNotNull(proxyFactory);
		WSNotification service = proxyFactory.getService(WSNotification.class);
		Assert.assertNotNull(service);

		// --- call
		WSNotificationCaller wsNotificationCaller = new WSNotificationCaller(service);
		EnvoyerNotificationResponse response = wsNotificationCaller.notifier(request);
		Assert.assertNotNull(response);
	}

}
