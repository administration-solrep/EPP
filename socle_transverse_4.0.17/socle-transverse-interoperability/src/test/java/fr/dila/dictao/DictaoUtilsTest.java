package fr.dila.dictao;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.sword.wsdl.dictao.dvs.DVSResponseEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

public class DictaoUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testExtractManifestId() {
		try {

			String xmlFileName = "fr/dila/dictao/xades_sample.xml";
			InputStream is = getClass().getClassLoader().getResourceAsStream(xmlFileName);
			String manifestId = DictaoUtils.extractManifestId(is);

			Assert.assertEquals("TAO_1_SignedManifest", manifestId);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testDigestData() {

		try {
			String data = "Hello World";

			String expectedSha256 = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";
			String resultSha256 = DictaoUtils.digestData(data, "SHA-256");
			Assert.assertEquals(expectedSha256, resultSha256);

		} catch (DictaoUtilsException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testIsSignatureValid() throws Exception {

		VerifySignatureExResponse dvsResponse = new VerifySignatureExResponse();
		dvsResponse.setVerifySignatureExResult(new DVSResponseEx());

		dvsResponse.getVerifySignatureExResult().setDVSGlobalStatus(0);
		Assert.assertTrue(DictaoUtils.isSignatureValid(dvsResponse));

		dvsResponse.getVerifySignatureExResult().setDVSGlobalStatus(1);
		Assert.assertFalse(DictaoUtils.isSignatureValid(dvsResponse));

		dvsResponse.getVerifySignatureExResult().setDVSGlobalStatus(2);
		Assert.assertFalse(DictaoUtils.isSignatureValid(dvsResponse));
	}

}
