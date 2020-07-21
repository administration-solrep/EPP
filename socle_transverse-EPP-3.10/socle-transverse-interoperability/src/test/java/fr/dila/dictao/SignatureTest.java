package fr.dila.dictao;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.dila.dictao.dvs.api.DVSOpStatus;
import fr.dila.dictao.dvs.proxy.DVSServiceCaller;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;
import junit.framework.Assert;

public class SignatureTest {

	private static final Logger	LOGGER	= Logger.getLogger(SignatureTest.class);

	@Test
	public void testSignAndVerify() throws Exception {

		String requestId = "NUM001";
		String dataToSign = "Hello World !";
		String dossierKey = "15_SENAT_QE_30";

		LOGGER.debug("start test testSignAndVerify");

		// -- build signatureExRequest
		SignatureEx signatureEx = D2SServiceCaller.buildRequest(requestId, dataToSign, "CreationSignature_DILA", dossierKey);
		Assert.assertNotNull(signatureEx);

		// --- sign data
		D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(null);
		Assert.assertNotNull(d2sServiceCaller);

		SignatureExResponse d2sResponse = d2sServiceCaller.signatureEx(signatureEx);
		Assert.assertNotNull(d2sResponse);

		String signature = new String(d2sResponse.getSignatureExResult().getD2SSignature().getBinaryValue().getValue());
		Assert.assertNotNull(signature);

		// --- build verify signature

		VerifySignatureEx verifySignatureEx = DVSServiceCaller.buildRequest(requestId, dataToSign, signature,
				"CreationSignature_DILA");
		Assert.assertNotNull(verifySignatureEx);

		DVSServiceCaller dvsServiceCaller = new DVSServiceCaller(null);
		Assert.assertNotNull(dvsServiceCaller);
		VerifySignatureExResponse dvsResponse = dvsServiceCaller.verifySignatureEx(verifySignatureEx);
		Assert.assertNotNull(dvsResponse);

		Assert.assertEquals(DVSOpStatus.OK, DVSOpStatus.fromInt(dvsResponse.getVerifySignatureExResult().getOpStatus()));
		Assert.assertTrue(DictaoUtils.isSignatureValid(dvsResponse));
	}

}
