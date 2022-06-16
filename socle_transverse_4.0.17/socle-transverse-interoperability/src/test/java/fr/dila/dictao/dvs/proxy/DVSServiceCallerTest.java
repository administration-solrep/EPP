package fr.dila.dictao.dvs.proxy;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.dila.dictao.d2s.stub.D2SServiceStub;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;


public class DVSServiceCallerTest {

	private static final Logger	LOGGER	= Logger.getLogger(DVSServiceCallerTest.class);
	
	private static final String DOSSIER_KEY = "15_SENAT_QE_30";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testBuildSignedDataHash() {

		try {
			String dataStr = "Hello World";
			String base64Ofsha256 = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";

			String expected = "<SignedDataHash>" + "<Manifest Id=\"IdNœudManifest\">" + "<Ref>"
					+ "<DigestMethod>SHA256</DigestMethod>" + "<DigestValue>" + base64Ofsha256 + "</DigestValue>"
					+ "</Ref>" + "</Manifest>" + "</SignedDataHash>";

			String result = DVSServiceCaller.buildSignedDataHash("IdNœudManifest", dataStr);

			LOGGER.debug("-- testBuildSignedDataHash");
			LOGGER.debug("data to sign: " + dataStr);
			LOGGER.debug("base64 of sha256 : " + base64Ofsha256);
			LOGGER.debug("result = \n" + result);
			Assert.assertNotNull(result);
			Assert.assertEquals(expected, result);

		} catch (DVSServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBuildRequest() {

		try {
			// --- setup
			String requestId = "REQUESTID";

			String data = "Hello World";

			String signatureFilename = "fr/dila/dictao/xades_sample.xml";

			// --- load data
			InputStream is = getClass().getClassLoader().getResourceAsStream(signatureFilename);
			String signature = InteropUtils.inputStreamToString(is);

			VerifySignatureEx request = DVSServiceCaller.buildRequest(requestId, data, signature,
					"CreationSignature_DILA");

			Assert.assertEquals(requestId, request.getRequestId());
			Assert.assertEquals(0, request.getRefreshCRLs());
			Assert.assertEquals("Tag", request.getTag());

			Assert.assertNotNull(request.getSignature());
			Assert.assertNotNull(request.getSignature().getBinaryValue());

			Assert.assertNull(request.getSignature().getBinaryValue().getDataFormat());

			LOGGER.debug("-- testBuildRequest");
			LOGGER.debug("request=\n" + JaxBHelper.marshallToString(request, VerifySignatureEx.class));

		} catch (DVSServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testVerifySignatureExNull() throws Exception {

		// --- call d2S stub ro
		SignatureEx d2sRequest = D2SServiceCaller.buildRequest("requestId", "Lorem Ipsum", "transactionId", DOSSIER_KEY);
		D2SServiceStub d2sServiceStub = new D2SServiceStub();
		SignatureExResponse d2sResponse = d2sServiceStub.signatureEx(d2sRequest);

		String signature = new String(d2sResponse.getSignatureExResult().getD2SSignature().getBinaryValue().getValue());

		// --- call DVS
		VerifySignatureEx request = DVSServiceCaller.buildRequest("requestId", "Lorem Ipsum", signature,
				"transactionId");
		Assert.assertNotNull(request);

		DVSServiceCaller caller = new DVSServiceCaller((String) null);
		Assert.assertNotNull(caller);

		VerifySignatureExResponse response = caller.verifySignatureEx(request);
		Assert.assertNotNull(response);

	}

}
