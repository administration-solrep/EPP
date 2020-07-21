package fr.dila.dictao.d2s.stub;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

public class D2SServiceStubTest {

	private static final String		FILE_BASEPATH					= "fr/dila/st/soap/stub/d2s/";
	private static final String		FILENAME_SIGNATUREEX_REQUEST	= "D2SInterfaceFrontEnd_signatureExRequest.xml";

	private static D2SServiceStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new D2SServiceStub();
		Assert.assertNotNull(stub);

	}

	@Test
	public void testSignatureEx() {

		try {
			SignatureEx request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH + FILENAME_SIGNATUREEX_REQUEST,
					SignatureEx.class);
			Assert.assertNotNull(request);
			SignatureExResponse response = stub.signatureEx(request);
			Assert.assertNotNull(response);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
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
			e.printStackTrace();
			Assert.fail("UnsupportedOperationException should be thrown");
		}

	}

	@Test
	public void testPrepareSignatureEx() {

		try {
			stub.prepareSignatureEx(null);
			Assert.fail("UnsupportedOperationException should be thrown");
		} catch (UnsupportedOperationException e) {
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("UnsupportedOperationException should be thrown");
		}
	}

}
