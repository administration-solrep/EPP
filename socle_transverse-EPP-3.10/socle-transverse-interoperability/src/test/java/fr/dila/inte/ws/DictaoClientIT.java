package fr.dila.inte.ws;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.dila.dictao.dvs.api.DVSOpStatus;
import fr.dila.dictao.dvs.proxy.DVSServiceCaller;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;
import junit.framework.Assert;

public class DictaoClientIT {

	private static final Logger	LOGGER							= Logger.getLogger(DictaoClientIT.class);

	private static final String	D2S_SERVICE_URL_HTTP			= "http://server1:8180/solrep-ws-server-stub/ws/soap/D2S";
	private static final String	DVS_SERVICE_URL_HTTP			= "http://server1:8180/solrep-ws-server-stub/ws/soap/DVS";

	private static final String	D2S_SERVICE_URL_HTTPS_CLIENT1	= "https://server1:8145/solrep-ws-server-stub/ws/soap/D2S";
	private static final String	DVS_SERVICE_URL_HTTPS_CLIENT1	= "https://server1:8145/solrep-ws-server-stub/ws/soap/DVS";

	private static final String	D2S_SERVICE_URL_HTTPS_CLIENT2	= "https://server2:8146/solrep-ws-server-stub/ws/soap/D2S";
	private static final String	DVS_SERVICE_URL_HTTPS_CLIENT2	= "https://server2:8146/solrep-ws-server-stub/ws/soap/DVS";

	private static final String	KEYSTORE_FILE					= "src/test/resources/fr/dila/dictao/local/keystore_client.jks";
	private static final String	KEYSTORE_PASS					= "secret";
	private static final String	KEYSTORE_FORMAT					= "JKS";

	private static final String	TRUSTSTORE_FILE					= "src/test/resources/fr/dila/dictao/local/truststore_client.jks";
	private static final String	TRUSTSTORE_PASS					= "secret";
	private static final String	TRUSTSTORE_FORMAT				= "JKS";

	private static final String	REQUEST_ID						= "REQUEST_ID_20110518_001";
	private static final String	TRANSACTION_ID_D2S				= "CreationSignature_DILA";
	private static final String	TRANSACTION_ID_DVS				= "ValidationSignature_DILA";
	private static final String	DATA_TO_SIGN					= "Hello World";
	
	private static final String DOSSIER_KEY = "15_SENAT_QE_30";

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

		LOGGER.info("-- testHttp");

		// --- sign data
		String signature = this.runD2s(D2S_SERVICE_URL_HTTP, null);

		// --- verify data
		this.runDvs(DVS_SERVICE_URL_HTTP, signature, null);
	}

	@Test
	public void testHttpsClient1() throws Exception {

		LOGGER.info("-- testHttps");

		// --- sign data
		String signature = this.runD2s(D2S_SERVICE_URL_HTTPS_CLIENT1, null);

		// --- verify data
		this.runDvs(DVS_SERVICE_URL_HTTPS_CLIENT1, signature, null);
	}

	@Test
	public void testHttpClient2() throws Exception {

		LOGGER.info("-- testHttp, set keyAlias");

		// --- sign data
		String signature = this.runD2s(D2S_SERVICE_URL_HTTP, "client2");

		// --- verify data
		this.runDvs(DVS_SERVICE_URL_HTTPS_CLIENT1, signature, "client2");

	}

	@Test
	public void testHttpsClient2() throws Exception {

		LOGGER.info("-- testHttps");

		// --- sign data
		String signature = this.runD2s(D2S_SERVICE_URL_HTTPS_CLIENT2, "client2");

		// --- verify data
		this.runDvs(DVS_SERVICE_URL_HTTPS_CLIENT2, signature, "client2");
	}

	private String runD2s(String url, String keyAlias) throws Exception {
		// --- build request
		SignatureEx request = D2SServiceCaller.buildRequest(REQUEST_ID, DATA_TO_SIGN, TRANSACTION_ID_D2S, DOSSIER_KEY);
		Assert.assertNotNull(request);

		// --- get caller
		D2SServiceCaller caller = new D2SServiceCaller(url);
		Assert.assertNotNull(caller);

		if (keyAlias != null) {
			caller.setClientKeyAlias(keyAlias);
		}

		// --- consume d2s
		SignatureExResponse response = caller.signatureEx(request);
		Assert.assertNotNull(response);

		String signature = new String(response.getSignatureExResult().getD2SSignature().getBinaryValue().getValue());

		return signature;

	}

	private void runDvs(String url, String signature, String keyAlias) throws Exception {
		// --- get caller
		DVSServiceCaller caller = new DVSServiceCaller(url);
		Assert.assertNotNull(caller);

		if (keyAlias != null) {
			caller.setClientKeyAlias(keyAlias);
		}

		VerifySignatureExResponse response = caller.verifySignatureEx(REQUEST_ID, DATA_TO_SIGN, signature,
				TRANSACTION_ID_DVS);
		Assert.assertNotNull(response);
		Assert.assertEquals(DVSOpStatus.OK.toInt(), response.getVerifySignatureExResult().getOpStatus());

	}

}
