package fr.dila.dictao.d2s.proxy;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import junit.framework.Assert;

public class D2SServiceCallerTest {

	private static final Logger		LOGGER			= Logger.getLogger(D2SServiceCallerTest.class);
	protected static final String	DIGEST_METHOD	= "SHA256";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testDigestDataString() {

		try {
			String dataStr = "Hello World";

			String expected = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";
			String result = D2SServiceCaller.digestData(dataStr);

			LOGGER.debug("--- testDigestData");
			LOGGER.debug("expected=" + expected);
			LOGGER.debug("result=" + result);

			Assert.assertEquals(expected, result);

		} catch (D2SServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBuildParameterString() {

		try {
			String dataStr = "Hello World";
			String digestValue = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";
			String uri = "file://some-file-uri";

			String expected = "<Parameters><Manifest><Reference>" + "<DigestValue>" + digestValue + "</DigestValue>"
					+ "<DigestMethod>" + DIGEST_METHOD + "</DigestMethod>" + "<URI>" + uri + "</URI>"
					+ "</Reference></Manifest></Parameters>";

			String result = D2SServiceCaller.buildParameterString(dataStr, uri);

			Assert.assertEquals(expected, result);

			LOGGER.debug("--- testBuildParameterString");
			LOGGER.debug("expected=\n" + result);
			LOGGER.debug("result=" + result);
		} catch (D2SServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSignatureEx() {

		try {
			String requestId = "REQUEST_ID_D2S_REPONSES_001";

			String filename = "fr/dila/dictao/testReponseToSign.txt";

			InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
			String dataStr = InteropUtils.inputStreamToString(is);

			D2SServiceCaller caller = new D2SServiceCaller((String) null);
			SignatureExResponse response = caller.signatureEx(requestId, dataStr, "CreationSignature_DILA", "15_SENAT_QE_30");
			Assert.assertNotNull(response);

		} catch (D2SServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

}
