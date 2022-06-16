package fr.dila.dictao.dvs.proxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

/**
 * Test a real DVSService
 * 
 * @author fbarmes
 * 
 */
public class DVSServiceCallerRunner {

	private static final boolean	RUN_SECURE				= false;
	private static final boolean	DEBUG_SSL				= false;

	private static final Logger		LOGGER					= Logger.getLogger(DVSServiceCallerRunner.class);

	private static final String		SERVICE_WSDL_URL_HTTP	= "http://localhost:8180/solrep-ws-server-stub/ws/soap/DVS";
	private static final String		SERVICE_WSDL_URL_HTTPS	= "https://sword-solon-dev:8643/solrep-ws-server-stub/ws/soap/DVS";

	private static final String		KEYSTORE_FILE			= "src/test/resources/fr/dila/dictao/user.p12";
	private static final String		KEYSTORE_PASS			= "secret";
	private static final String		KEYSTORE_FORMAT			= "PKCS12";

	private static final String		TRUSTSTORE_FILE			= "src/test/resources/fr/dila/dictao/cacerts.jks";
	private static final String		TRUSTSTORE_PASS			= "secret";
	private static final String		TRUSTSTORE_FORMAT		= "JKS";

	private static final String		REQUEST_ID				= "REQUEST_ID";

	private static final String		DATA_TO_SIGN			= "src/test/resources/fr/dila/dictao/testReponseToSign.txt";
	private static final String		XML_FILENAME			= "fr/dila/dictao/xades_sample.xml";

	public static void main(String[] args) {

		LOGGER.info("Start secure mode = " + RUN_SECURE);
		try {
			if (RUN_SECURE) {
				runHttps();
			} else {
				runHttp();
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			LOGGER.error(e.getMessage());
			LOGGER.error(sw.toString());
		}
		LOGGER.info("Done");
	}

	private static void runHttp() throws Exception {
		run(SERVICE_WSDL_URL_HTTP);
	}

	private static void runHttps() throws Exception {

		// --- set required system properties
		Properties systemProps = System.getProperties();
		if (DEBUG_SSL) {
			systemProps.put("javax.net.debug", "all");
		}

		LOGGER.info("Set HTTPS related system properties");
		systemProps.put("javax.net.ssl.keyStore", KEYSTORE_FILE);
		systemProps.put("javax.net.ssl.keyStorePassword", KEYSTORE_PASS);
		systemProps.put("javax.net.ssl.keyStoreType", KEYSTORE_FORMAT);

		systemProps.put("javax.net.ssl.trustStore", TRUSTSTORE_FILE);
		systemProps.put("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASS);
		systemProps.put("javax.net.ssl.trustStoreType", TRUSTSTORE_FORMAT);

		run(SERVICE_WSDL_URL_HTTPS);
	}

	private static void run(String url) throws Exception {

		LOGGER.info("load data");
		InputStream signatureIs = DVSServiceCallerRunner.class.getClassLoader().getResourceAsStream(XML_FILENAME);
		String signature = InteropUtils.inputStreamToString(signatureIs);

		InputStream dataIs = new FileInputStream(DATA_TO_SIGN);
		String dataStr = InteropUtils.inputStreamToString(dataIs);

		LOGGER.info("BuildRequest");
		VerifySignatureEx verifySignatureEx = DVSServiceCaller.buildRequest(REQUEST_ID, dataStr, signature,
				"CreationSignature_DILA");

		String requestStr = JaxBHelper.marshallToString(verifySignatureEx, VerifySignatureEx.class);
		LOGGER.info("\n" + requestStr);

		LOGGER.info("Communication");
		DVSServiceCaller dvsServiceCaller = new DVSServiceCaller(url);
		VerifySignatureExResponse response = dvsServiceCaller.verifySignatureEx(verifySignatureEx);

		if (response == null) {
			LOGGER.info("response is null");
		}

		LOGGER.info("Handle response");
		String responseStr = JaxBHelper.marshallToString(response, VerifySignatureExResponse.class);
		LOGGER.info("\n" + responseStr);
	}

}
