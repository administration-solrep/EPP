package fr.dila.dictao.d2s.proxy;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import fr.dila.dictao.DictaoUtils;
import fr.dila.dictao.DictaoUtilsException;
import fr.dila.dictao.d2s.api.D2SOpStatus;
import fr.dila.dictao.d2s.stub.D2SServiceStub;
import fr.dila.dictao.proxy.DictaoServiceCaller;
import fr.dila.dictao.proxy.DictaoServiceCallerException;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.d2s.D2S;
import fr.sword.wsdl.dictao.d2s.D2SSoap;
import fr.sword.wsdl.dictao.d2s.DataBinary;
import fr.sword.wsdl.dictao.d2s.DataType;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

/**
 * D2SServiceProxy wrapper.<br/>
 * This class encapsulate calls to the D2s proxy.
 * 
 * @author fbarmes
 * 
 */
public class D2SServiceCaller extends DictaoServiceCaller {

	protected static final Logger	LOGGER	= Logger.getLogger(D2SServiceCaller.class);

	private final D2SSoap			d2sService;

	/**
	 * Construct the service caller to the specified url
	 * 
	 * @param url
	 *            the url of the service. If null, a stub is used
	 */
	public D2SServiceCaller(String url) {
		super(url);

		if (url == null) {
			this.d2sService = new D2SServiceStub();

		} else {
			URL wsdlUrl = D2SServiceCaller.class.getResource(D2S_WSDL_FILE);
			D2S d2s = new D2SServiceProxy(wsdlUrl);
			this.d2sService = d2s.getD2SSoap();

			BindingProvider bindingProvider = (BindingProvider) this.d2sService;
			Map<String, Object> properties = bindingProvider.getRequestContext();
			properties.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param d2sService
	 */
	// @Deprecated
	// public D2SServiceCaller(D2SSoap d2sService) {
	// super(null);
	// this.d2sService = d2sService;
	// }

	/**
	 * Specify the client key alias to use during the ssl handshake
	 * 
	 * @param alias
	 * @throws D2SServiceCallerException
	 */
	public void setClientKeyAlias(String alias) throws DictaoServiceCallerException {
		setClientKeyAlias(this.d2sService, alias);
	}

	/**
	 * Wrapper to the signatureEx method
	 * 
	 * @param requestId
	 *            a unique id for the request generated by the caller application
	 * @param transactionId
	 *            a unique id for the transaction generated by the caller application
	 * @param data
	 *            the data to sign
	 * @return a SignatureExResponxe
	 * @throws D2SServiceCallerException
	 */
	public SignatureExResponse signatureEx(String requestId, String data, String transactionId, String dossierKey)
			throws D2SServiceCallerException {
		SignatureEx request = buildRequest(requestId, data, transactionId, dossierKey);
		return this.signatureEx(request);
	}

	/**
	 * Wrapper to the signaureEx method with a complete request
	 * 
	 * @param request
	 * @return
	 * @throws D2SServiceCallerException
	 */
	public SignatureExResponse signatureEx(SignatureEx request) throws D2SServiceCallerException {

		// --- handle transaction
		SignatureExResponse response = this.d2sService.signatureEx(request);

		// ---- log transaction
		try {
			LOGGER.info(JaxBHelper.logOutWsTransaction(this.getUrl(), null, this.getClientKeyAlias(), request, response));
		} catch (JAXBException e) {
			LOGGER.error("Could not log D2S transaction", e);
		}

		// --- check response
		if (response == null || response.getSignatureExResult() == null) {
			throw new D2SServiceCallerException("Could not retrieve SignatureExResponse : null");
		}

		int opStatus = response.getSignatureExResult().getOpStatus();
		D2SOpStatus status = D2SOpStatus.fromInt(opStatus);

		// --- handle response status
		if (status != D2SOpStatus.OK) {
			throw new D2SServiceCallerException("bad D2S response status : " + status.toString());
		}

		// --- return response
		return response;
	}

	/**
	 * Utility function used to buid a SignatureEx request object
	 * 
	 * @param requestId
	 * @param transactionId
	 * @param data
	 * @param dossierKey l'URI à renseigner dans l'appel
	 * @return
	 * @throws D2SServiceCallerException
	 */
	public static final SignatureEx buildRequest(String requestId, String data, String transactionId, String dossierKey)
			throws D2SServiceCallerException {
		// --- encode Manifest
		InputStream inputStream = D2SServiceCaller.class.getClassLoader().getResourceAsStream(D2S_MANIFEST_FILENAME);
		byte[] manifestBytes = D2SServiceCaller.get_file_content(inputStream);

		// --- build request
		SignatureEx request = new SignatureEx();
		request.setRequestId(requestId);
		request.setTransactionId(transactionId);
		request.setTag(D2S_TAG);
		request.setSignatureFormat(D2S_SIGNATURE_FORMAT);
		request.setSignatureType(D2S_SIGNATURE_TYPE);

		request.setDataToSign(getDataTypeFromBytes(manifestBytes));
		request.setSignatureParameter(buildParameterString(data, dossierKey));

		return request;
	}

	/**
	 * Analyses the responses and retrieve the actuel signature part
	 * 
	 * @param response
	 * @return
	 */
	public static String extractSignatureFromResponse(SignatureExResponse response) {

		try {
			return new String(response.getSignatureExResult().getD2SSignature().getBinaryValue().getValue());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Utility function, make a digest of the data to sign
	 * 
	 * @param dataToSign
	 *            byte array representation of the data
	 * @return a string
	 * @throws D2SServiceCallerException
	 * @throws NoSuchAlgorithmException
	 */
	protected static final String digestData(String dataToSign) throws D2SServiceCallerException {
		try {
			return DictaoUtils.digestData(dataToSign, DIGEST_METHOD_FOR_JAVA);
		} catch (DictaoUtilsException e) {
			throw new D2SServiceCallerException(e);
		}
	}

	/**
	 * Utility function that build the parameterString for the request
	 * 
	 * @param data
	 * @param uri
	 * @return
	 * @throws D2SServiceCallerException
	 */
	protected static final String buildParameterString(String data, String uri) throws D2SServiceCallerException {

		String digestValue = digestData(data);

		StringBuffer parameterString = new StringBuffer();

		parameterString.append("<Parameters>").append("<Manifest>").append("<Reference>").append("<DigestValue>")
				.append(digestValue).append("</DigestValue>").append("<DigestMethod>").append(DIGEST_METHOD)
				.append("</DigestMethod>").append("<URI>").append(uri).append("</URI>").append("</Reference>")
				.append("</Manifest>").append("</Parameters>");

		return parameterString.toString();
	}

	/**
	 * Utility function, converts a byte array to the DataType required Uses base64Enc
	 * 
	 * @param bytes
	 * @return
	 */
	protected static final DataType getDataTypeFromBytes(byte[] bytes) {
		DataType dataType = null;
		if (bytes != null && bytes.length > 0) {
			DataBinary dataBinary = new DataBinary();
			dataBinary.setValue(bytes);
			dataType = new DataType();
			dataType.setBinaryValue(dataBinary);
		}
		return dataType;
	}

	/**
	 * Transform the given input stream into a ByteArray
	 * 
	 * @param inputStream
	 * @return
	 * @throws D2SServiceCallerException
	 */
	protected static final byte[] get_file_content(InputStream inputStream) throws D2SServiceCallerException {

		try {
			byte[] temp = null;
			int tempSize;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			temp = new byte[2048];
			while ((tempSize = inputStream.read(temp)) >= 0) {
				baos.write(temp, 0, tempSize);
			}
			inputStream.close();

			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			throw new D2SServiceCallerException(e);
		} catch (IOException e) {
			throw new D2SServiceCallerException(e);
		}

	}

}
