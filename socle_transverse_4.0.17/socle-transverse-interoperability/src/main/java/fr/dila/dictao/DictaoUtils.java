package fr.dila.dictao;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.dila.dictao.d2s.proxy.D2SServiceCallerException;
import fr.dila.st.utils.SecureEntityResolver;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

/**
 * A collection of static utilities when dealing with dictao web services
 * 
 * @author fbarmes
 * 
 */
public class DictaoUtils {

	/**
	 * make a digest of the data to sign
	 * 
	 * @param dataToSign
	 *            byte array representation of the data
	 * @return a string
	 * @throws D2SServiceCallerException
	 * @throws NoSuchAlgorithmException
	 */
	@Deprecated
	protected static final String digestDataOld(String dataToSign, String digestMethod) throws DictaoUtilsException {
		try {

			// --- digest data
			MessageDigest md = MessageDigest.getInstance(digestMethod);
			md.update(dataToSign.getBytes());
			byte[] digest = md.digest();

			// --- transform digest to ASCII string
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new DictaoUtilsException(e);
		}
	}

	/**
	 * make a digest of the data to sign
	 * 
	 * @param data
	 *            byte array representation of the data
	 * @return a string
	 * @throws D2SServiceCallerException
	 * @throws NoSuchAlgorithmException
	 */
	public static final String digestData(String data, String digestMethod) throws DictaoUtilsException {
		try {
			MessageDigest md = MessageDigest.getInstance(digestMethod);
			md.update(data.getBytes());
			byte[] digest = md.digest();

			String b64string = Base64.encodeBase64String(digest);
			b64string = b64string.trim();

			return b64string;
		} catch (NoSuchAlgorithmException e) {
			throw new DictaoUtilsException(e);
		}
	}

	/**
	 * extra ct the manifest if from the is.
	 * 
	 * @param is
	 *            the inputStream, should contain a valid XML
	 * @return the manifest
	 * @throws DictaoUtilsException
	 */
	public static final String extractManifestId(InputStream is) throws DictaoUtilsException {

		try {
			String expression = "/Signature/Object[1]/Manifest/@Id";

			// --- create DOM
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			documentBuilder.setEntityResolver(new SecureEntityResolver());
			Document xmlDocument = documentBuilder.parse(is);

			// --- create Xpath
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			XPathExpression xPathExpression = xPath.compile(expression);

			// --- get result
			Object result = xPathExpression.evaluate(xmlDocument, XPathConstants.STRING);

			if (result == null) {
				return null;
			} else {
				return result.toString();
			}

		} catch (SAXException e) {
			throw new DictaoUtilsException("Can not parse InputStream", e);
		} catch (Exception e) {
			throw new DictaoUtilsException(e);
		}

	}

	/**
	 * Encode to base64
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeBase64(String str) {
		return Base64.encodeBase64String(str.getBytes());
	}

	/**
	 * Assert wether the result of a dvs verifySignature indictes a valide signature
	 * 
	 * @param dvsResponse
	 * @return true if the signature is valid, false otherwise
	 * @throws DictaoUtilsException
	 */
	public static boolean isSignatureValid(VerifySignatureExResponse dvsResponse) throws DictaoUtilsException {

		if (dvsResponse == null) {
			throw new DictaoUtilsException("dvsResponse is null");
		}

		if (dvsResponse.getVerifySignatureExResult() == null) {
			throw new DictaoUtilsException("dvsResponse.verifySignatureEx is null");
		}

		int dvsGlobalstatus = dvsResponse.getVerifySignatureExResult().getDVSGlobalStatus();

		return dvsGlobalstatus == 0;
	}

}
