package fr.dila.dictao.d2s.proxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.dictao.dvs.api.DVSOpStatus;
import fr.dila.dictao.dvs.proxy.DVSServiceCaller;
import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;


public class D2SServiceCallerRunnerQualif {

	private static final Logger	LOGGER						= Logger.getLogger(D2SServiceCallerRunnerQualif.class);

	private static final String	D2S_SERVICE_URL				= "https://dxsqualif.dictao.com:22543/D2SInterface/D2SInterfaceSoap.cgi";
	private static final String	DVS_SERVICE_URL				= "https://dxsqualif.dictao.com:24543/DVSInterface/DVSInterfaceSoap.cgi";

	private static final String	DICTAO_KEYSTORE_FILE		= "src/test/resources/fr/dila/dictao/qualif/ApplicationDILA.p12";
	private static final String	DICTAO_KEYSTORE_PASS		= "password";
	private static final String	DICTAO_KEYSTORE_FORMAT		= "PKCS12";

	private static final String	DICTAO_TRUSTSTORE_FILE		= "src/test/resources/fr/dila/dictao/qualif/cacerts.jks";
	private static final String	DICTAO_TRUSTSTORE_PASS		= "changeit";
	private static final String	DICTAO_TRUSTSTORE_FORMAT	= "JKS";

	private static final String	DATA_TO_SIGN				= "src/test/resources/fr/dila/dictao/testReponseToSign.txt";
	private static final String	REQUEST_ID					= "REQUEST_ID_20110518_001";
	private static final String	TRANSACTION_ID_CREATION		= "CreationSignature_DILA";
	private static final String	TRANSACTION_ID_VERIFICATION	= "ValidationSignature_DILA";
	
	private static final String DOSSIER_KEY = "15_SENAT_QE_30";

	@BeforeClass
	public static void setupBeforeClass() throws Exception {

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
	public void testRoundTrip() throws Exception {

		InputStream is = new FileInputStream(DATA_TO_SIGN);
		String dataToSign = InteropUtils.inputStreamToString(is);

		LOGGER.info("=== Sign data");
		String signature = signData(dataToSign);

		LOGGER.info("=== Verify data");
		verifyData(dataToSign, signature);

		LOGGER.info("Verify signature");
	}

	private static String signData(String dataToSign) throws Exception {

		SignatureEx request = D2SServiceCaller.buildRequest(REQUEST_ID, dataToSign, TRANSACTION_ID_CREATION, DOSSIER_KEY);

		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(D2S_SERVICE_URL);
		Assert.assertNotNull(d2sServiceCaller);

		SignatureExResponse response = d2sServiceCaller.signatureEx(request);
		Assert.assertNotNull(response);

		String signature = new String(response.getSignatureExResult().getD2SSignature().getBinaryValue().getValue());
		Assert.assertNotNull(signature);
		Assert.assertTrue(signature.length() > 0);

		LOGGER.info("signature cleartext=\n" + signature);
		return signature;
	}

	private static void verifyData(String data, String signature) throws Exception {

		DVSServiceCaller dvsServiceCaller = new DVSServiceCaller(DVS_SERVICE_URL);
		Assert.assertNotNull(dvsServiceCaller);

		VerifySignatureExResponse response = dvsServiceCaller.verifySignatureEx(REQUEST_ID, data, signature,
				TRANSACTION_ID_VERIFICATION);
		Assert.assertNotNull(response);
		Assert.assertEquals(DVSOpStatus.OK, response.getVerifySignatureExResult().getOpStatus());
	}

}
