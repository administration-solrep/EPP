package fr.dila.inte.ws;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import org.junit.Assert;

public class WsClientIntegrationTest {

	private static final String	D2S_SERVICE_URL_AUTH		= "https://server1:8145/solrep-ws-server-stub/ws/soap/D2S";
	private static final String	D2S_SERVICE_URL_NOAUTH		= "https://server1:8144/solrep-ws-server-stub/ws/soap/D2S";

	private static final String	DICTAO_KEYSTORE_FILE		= "/home/admin/ssl/mutual-auth-2-server/keystore_client.jks";
	private static final String	DICTAO_KEYSTORE_PASS		= "secret";
	private static final String	DICTAO_KEYSTORE_FORMAT		= "JKS";

	private static final String	DICTAO_TRUSTSTORE_FILE		= "/home/admin/ssl/mutual-auth-2-server/truststore_client.jks";
	private static final String	DICTAO_TRUSTSTORE_PASS		= "secret";
	private static final String	DICTAO_TRUSTSTORE_FORMAT	= "JKS";

	private static final String	REQUEST_ID					= "REQUEST_ID_20110518_001";
	private static final String	TRANSACTION_ID_CREATION		= "CreationSignature_DILA";
	private static final String	DATA_TO_SIGN				= "Hello World";
	
	private static final String DOSSIER_KEY = "15_SENAT_QE_30";

	@BeforeClass
	public static void setupBeforeClass() {
		Properties systemProps = System.getProperties();

		systemProps.put("javax.net.debug", "ssl,handshake");

		systemProps.put("javax.net.ssl.keyStore", DICTAO_KEYSTORE_FILE);
		systemProps.put("javax.net.ssl.keyStorePassword", DICTAO_KEYSTORE_PASS);
		systemProps.put("javax.net.ssl.keyStoreType", DICTAO_KEYSTORE_FORMAT);

		systemProps.put("javax.net.ssl.trustStore", DICTAO_TRUSTSTORE_FILE);
		systemProps.put("javax.net.ssl.trustStorePassword", DICTAO_TRUSTSTORE_PASS);
		systemProps.put("javax.net.ssl.trustStoreType", DICTAO_TRUSTSTORE_FORMAT);

	}

	@Test
	public void testD2S() throws Exception {

		// -- get the service
		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(D2S_SERVICE_URL_AUTH);
		SignatureExResponse response = d2sServiceCaller.signatureEx(REQUEST_ID, DATA_TO_SIGN, TRANSACTION_ID_CREATION, DOSSIER_KEY);
		Assert.assertNotNull(response);

	}

	@Test
	public void testD2SWithCustomSSF() throws Exception {

		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(D2S_SERVICE_URL_AUTH);
		d2sServiceCaller.setClientKeyAlias("client2");
		SignatureExResponse response = d2sServiceCaller.signatureEx(REQUEST_ID, DATA_TO_SIGN, TRANSACTION_ID_CREATION, DOSSIER_KEY);
		Assert.assertNotNull(response);
	}

	@Test
	public void testD2SWithCustomSSFNoAuth() throws Exception {

		Properties systemProps = System.getProperties();
		systemProps.remove("javax.net.ssl.keyStore");
		systemProps.remove("javax.net.ssl.keyStorePassword");
		systemProps.remove("javax.net.ssl.keyStoreType");

		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(D2S_SERVICE_URL_NOAUTH);
		SignatureExResponse response = d2sServiceCaller.signatureEx(REQUEST_ID, DATA_TO_SIGN, TRANSACTION_ID_CREATION, DOSSIER_KEY);
		Assert.assertNotNull(response);
	}

}
