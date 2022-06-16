package fr.dila.dictao.d2s;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.dila.dictao.d2s.api.D2SService;
import fr.dila.dictao.d2s.proxy.D2SServiceProxy;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.d2s.DataBinary;
import fr.sword.wsdl.dictao.d2s.DataEncoding;
import fr.sword.wsdl.dictao.d2s.DataType;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

public class D2SclientRunner {

	private static final Logger	LOGGER				= Logger.getLogger(D2SclientRunner.class);

	private static final String	SERVICE_WSDL_URL	= "http://localhost:8180/solrep-ws-server-stub/ws/soap/D2S?wsdl";

	@Test
	public void runClient() {

		String xadesXml = "Hello World !";

		SignatureEx request = new SignatureEx();
		request.setRequestId("dummyRequestId");
		request.setTransactionId("dummyTransactionId");
		request.setTag("DummyTag");

		DataType dataToSign = new DataType();
		DataBinary dataBinary = new DataBinary();
		dataToSign.setBinaryValue(dataBinary);
		request.setDataToSign(dataToSign);

		dataBinary.setDataFormat(DataEncoding.B_64_ENC);
		dataBinary.setValue(Base64.encodeBase64(xadesXml.getBytes()));

		request.setSignatureFormat("XADES");
		request.setSignatureType("DETACHED");
		request.setSignatureParameter("<Parameters><Manifest><Reference><DigestValue>Condensé au format Base 64</DigestValue><DigestMethod>Algorithme du condensé</DigestMethod><URI>URI vers le fichier de données</URI></Reference></Manifest></Parameters>");

		// ----- init wsdl url
		try {
			URL wsdlUrl = new URL(SERVICE_WSDL_URL);

			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("user", "secret".toCharArray());
				}
			});

			D2SService serviceProxy = new D2SServiceProxy(wsdlUrl);

			SignatureExResponse response = serviceProxy.signatureEx(request);

			Assert.assertNotNull(response);
			Assert.assertNotNull(response.getSignatureExResult());

			LOGGER.info(JaxBHelper.marshallToString(response, SignatureExResponse.class));

		} catch (MalformedURLException e) {
			LOGGER.error("Malformed URL " + SERVICE_WSDL_URL, e);
			Assert.fail();
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
