package fr.dila.dictao.dvs.proxy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import fr.dila.dictao.DictaoUtils;
import fr.dila.dictao.DictaoUtilsException;
import fr.dila.dictao.dvs.api.DVSOpStatus;
import fr.dila.dictao.dvs.stub.DVSServiceStub;
import fr.dila.dictao.proxy.DictaoServiceCaller;
import fr.dila.dictao.proxy.DictaoServiceCallerException;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.dvs.DVS;
import fr.sword.wsdl.dictao.dvs.DVSSoap;
import fr.sword.wsdl.dictao.dvs.DataBinary;
import fr.sword.wsdl.dictao.dvs.DataType;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

public class DVSServiceCaller extends DictaoServiceCaller {

	protected static final Logger	LOGGER	= Logger.getLogger(DVSServiceCaller.class);

	private final DVSSoap			dvsService;

	/**
	 * Cosntructs a caller to the given url
	 * 
	 * @param url
	 */
	public DVSServiceCaller(String url) {
		super(url);

		if (url == null) {
			this.dvsService = new DVSServiceStub();
		} else {
			URL wsdlUrl = DVSServiceCaller.class.getResource(DVS_WSDL_FILE);
			DVS dvsService = new DVSServiceProxy(wsdlUrl);
			this.dvsService = dvsService.getDVSSoap();

			BindingProvider bindingProvider = (BindingProvider) this.dvsService;
			Map<String, Object> properties = bindingProvider.getRequestContext();
			properties.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		}

	}

	// @Deprecated
	// public DVSServiceCaller(DVSSoap dvsService) {
	// super(null);
	// this.dvsService = dvsService;
	// }

	@Override
	public void setClientKeyAlias(String alias) throws DictaoServiceCallerException {
		setClientKeyAlias(this.dvsService, alias);
	}

	public VerifySignatureExResponse verifySignatureEx(String requestId, String data, String signature,
			String transactionId) throws DVSServiceCallerException {
		VerifySignatureEx verifySignatureEx = buildRequest(requestId, data, signature, transactionId);
		return verifySignatureEx(verifySignatureEx);
	}

	public VerifySignatureExResponse verifySignatureEx(VerifySignatureEx verifySignatureEx)
			throws DVSServiceCallerException {
		// --- handle transaction
		VerifySignatureExResponse response = this.dvsService.verifySignatureEx(verifySignatureEx);

		// --- Log transaction
		try {
			LOGGER.info(JaxBHelper.logOutWsTransaction(this.getUrl(), null, this.getClientKeyAlias(),
					verifySignatureEx, response));
		} catch (JAXBException e) {
			LOGGER.error("Could not log DVS transaction", e);
		}

		// --- check response
		if (response == null || response.getVerifySignatureExResult() == null) {
			throw new DVSServiceCallerException("Could not retrieve VerifySignatureExResult : null");
		}

		int opStatus = response.getVerifySignatureExResult().getOpStatus();
		DVSOpStatus dvsOpStatus = DVSOpStatus.fromInt(opStatus);

		// --- handle response status
		if (dvsOpStatus != DVSOpStatus.OK) {
			throw new DVSServiceCallerException("bad DVS response status " + dvsOpStatus.toInt() + " "
					+ dvsOpStatus.toString());
		}

		// --- return response
		return response;
	}

	/**
	 * Utility function, build a VerifySignature request object
	 * 
	 * @param requestId
	 *            the request ID
	 * @param dataToVerify
	 *            the data that was signed
	 * @param signature
	 *            the signature obtained from d2s
	 * @param transactionId
	 *            the transactionId
	 * @return
	 * @throws DVSServiceCallerException
	 */
	public static VerifySignatureEx buildRequest(String requestId, String dataToVerify, String signature,
			String transactionId) throws DVSServiceCallerException {
		try {
			VerifySignatureEx request = new VerifySignatureEx();

			InputStream is = new ByteArrayInputStream(signature.getBytes("UTF-8"));
			String manifestId = DictaoUtils.extractManifestId(is);

			request.setRequestId(requestId);
			request.setTransactionId(transactionId);
			request.setRefreshCRLs(DVS_REFRESH_CRL);
			request.setTag(DVS_TAG);

			request.setSignedDataHash(buildSignedDataHash(manifestId, dataToVerify));
			request.setSignature(buildSignatureDataType(signature));

			return request;
		} catch (UnsupportedEncodingException e) {
			throw new DVSServiceCallerException(e);
		} catch (DictaoUtilsException e) {
			throw new DVSServiceCallerException(e);
		}
	}

	protected static String buildSignedDataHash(String manifestId, String data) throws DVSServiceCallerException {

		try {
			String b64String = DictaoUtils.digestData(data, DIGEST_METHOD_FOR_JAVA);

			StringBuffer sb = new StringBuffer();

			sb.append("<SignedDataHash>").append("<Manifest Id=\"" + manifestId + "\">").append("<Ref>")
					.append("<DigestMethod>" + DIGEST_METHOD + "</DigestMethod>")
					.append("<DigestValue>" + b64String + "</DigestValue>").append("</Ref>").append("</Manifest>")
					.append("</SignedDataHash>");

			return sb.toString();
		} catch (Exception e) {
			throw new DVSServiceCallerException(e);
		}
	}

	protected static DataType buildSignatureDataType(String signatureXml) {

		DataType dataType = new DataType();
		DataBinary dataBinary = new DataBinary();
		dataType.setBinaryValue(dataBinary);
		dataBinary.setValue(signatureXml.getBytes());

		return dataType;
	}

}
