package fr.dila.dictao.d2s.proxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

/**
 * Test a real D2SService
 * 
 * @author fbarmes
 * 
 */
public class D2SServiceCallerRunner {

	private static final Logger		LOGGER					= Logger.getLogger(D2SServiceCallerRunner.class);

	private static final boolean	DEBUG_SSL				= true;
	private static final boolean	RUN_SECURE				= false;

	private static final String		SERVICE_WSDL_URL_HTTP	= "http://localhost:8180/solrep-ws-server-stub/ws/soap/D2S";

	private static final String		SERVICE_WSDL_URL_HTTPS	= "https://sword-solon-dev:8643/solrep-ws-server-stub/ws/soap/D2S";

	private static final String		KEYSTORE_FILE			= "src/test/resources/fr/dila/dictao/user.p12";
	private static final String		KEYSTORE_PASS			= "secret";
	private static final String		KEYSTORE_FORMAT			= "PKCS12";

	private static final String		TRUSTSTORE_FILE			= "src/test/resources/fr/dila/dictao/cacerts.jks";
	private static final String		TRUSTSTORE_PASS			= "secret";
	private static final String		TRUSTSTORE_FORMAT		= "JKS";

	private static final String		DATA_TO_SIGN			= "src/test/resources/fr/dila/dictao/testReponseToSign.txt";

	private static final String		REQUEST_ID				= "REQUEST_ID";
	
	private static final String DOSSIER_KEY = "15_SENAT_QE_30";

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

	private static void runHttp() throws Exception {
		run(SERVICE_WSDL_URL_HTTP);
	}

	private static void run(String url) throws Exception {

		// --- prepare data

		LOGGER.info("Load data");
		InputStream is = new FileInputStream(DATA_TO_SIGN);
		String dataStr = InteropUtils.inputStreamToString(is);

		LOGGER.info("BuildRequest");
		SignatureEx request = D2SServiceCaller.buildRequest(REQUEST_ID, dataStr, "CreationSignature_DILA", DOSSIER_KEY);
		String requestStr = JaxBHelper.marshallToString(request, SignatureEx.class);
		LOGGER.info("\n" + requestStr);

		LOGGER.info("Communication");
		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(url);

		SignatureExResponse response = d2sServiceCaller.signatureEx(request);
		if (response == null) {
			LOGGER.info("response is null");
		}

		LOGGER.info("Handle response");
		String responseStr = JaxBHelper.marshallToString(response, SignatureExResponse.class);
		LOGGER.info("\n" + responseStr);

	}

}
