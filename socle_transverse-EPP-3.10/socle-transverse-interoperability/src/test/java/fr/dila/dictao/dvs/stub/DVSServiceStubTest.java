package fr.dila.dictao.dvs.stub;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

public class DVSServiceStubTest {

	private static DVSServiceStub	stub;

	private static final String		FILE_BASEPATH					= "fr/dila/st/soap/stub/dvs/";
	private static final String		FILENAME_SIGNATUREEX_REQUEST	= "DVSInterfaceFrontEnd_verifySignatureExRequest.xml";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		stub = new DVSServiceStub();
		Assert.assertNotNull(stub);

	}

	@Test
	public void testVerifySignatureEx() {

		try {
			VerifySignatureEx request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH + FILENAME_SIGNATUREEX_REQUEST,
					VerifySignatureEx.class);
			Assert.assertNotNull(request);

			VerifySignatureExResponse response = stub.verifySignatureEx(request);
			Assert.assertNotNull(response);

		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testVerifyCertificateEx() {
		try {
			stub.verifyCertificateEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testVerifyAuthenticationEx() {
		try {
			stub.verifyAuthenticationEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testGetAuthenticationChallengeEx() {
		try {
			stub.getAuthenticationChallengeEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testGetArchiveEx() {
		try {
			stub.getArchiveEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testPrepareAuthenticationRequestEx() {
		try {
			stub.prepareAuthenticationRequestEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testCustomizeTokenEx() {
		try {
			stub.customizeTokenEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

}
