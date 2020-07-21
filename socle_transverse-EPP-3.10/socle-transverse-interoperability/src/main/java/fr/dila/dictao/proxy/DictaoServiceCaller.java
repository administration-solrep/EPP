package fr.dila.dictao.proxy;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;

import fr.dila.dictao.d2s.proxy.D2SServiceCallerException;
import fr.dila.st.utils.ssl.SSLSocketFactoryBuilder;

public abstract class DictaoServiceCaller {

	private static final String		HTTPS_REGEX				= "(?i)https://.*";

	protected static final String	D2S_WSDL_FILE			= "/wsdl/dictao/d2s/D2SInterfaceFrontEnd.wsdl";
	protected static final String	DVS_WSDL_FILE			= "/wsdl/dictao/dvs/DVSInterfaceFrontEnd.wsdl";

	protected static final String	DIGEST_METHOD			= "SHA256";
	protected static final String	DIGEST_METHOD_FOR_JAVA	= "SHA-256";

	protected static final String	D2S_TAG					= "Tag";
	protected static final String	D2S_SIGNATURE_FORMAT	= "XADES";
	protected static final String	D2S_SIGNATURE_TYPE		= "DETACHED";

	protected static final String	D2S_MANIFEST_FILENAME	= "wsdl/dictao/d2s/Manifest.xml";				;

	protected static final int		DVS_REFRESH_CRL			= 0;
	protected static final String	DVS_TAG					= "Tag";

	private String					url;
	private String					clientKeyAlias;

	public DictaoServiceCaller(String url) {
		super();
		this.url = url;
	}

	/**
	 * Check if the service is set to use secure connexions
	 * 
	 * @param service
	 *            the service Object. Should be an instance of {@link BindingProvider}. Use the service port object for
	 *            this.
	 * @return
	 */
	protected static boolean isSecure(Object service) {

		if (!(service instanceof BindingProvider)) {
			return false;
		}

		BindingProvider provider = (BindingProvider) service;
		Map<String, Object> properties = provider.getRequestContext();
		if (properties == null) {
			return false;
		}

		String url = (String) properties.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
		return isSecure(url);
	}

	/**
	 * Check if the provided url is secure (i.e. https)
	 * 
	 * @param url
	 * @return
	 */
	private static boolean isSecure(String url) {
		if (url == null) {
			return false;
		} else {
			return url.matches(HTTPS_REGEX);
		}
	}

	/**
	 * Set the lcient alias to use for secure communications
	 * 
	 * @param alias
	 * @throws DictaoServiceCallerException
	 */
	public abstract void setClientKeyAlias(String alias) throws DictaoServiceCallerException;

	/**
	 * Set the client Key alias to use for secure communications on the provided service
	 * 
	 * @param service
	 * @param alias
	 * @throws D2SServiceCallerException
	 */
	public void setClientKeyAlias(Object service, String alias) throws DictaoServiceCallerException {

		this.clientKeyAlias = alias;

		try {
			if (!isSecure(service)) {
				return;
			}

			if (service instanceof BindingProvider) {
				BindingProvider bindingProvider = (BindingProvider) service;
				SSLSocketFactoryBuilder sslfg = new SSLSocketFactoryBuilder(alias);
				SSLSocketFactory sslf = sslfg.getSSLSocketFactory("TLS");
				bindingProvider.getRequestContext().put(com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY,
						sslf);
			}
		} catch (IOException e) {
			throw new DictaoServiceCallerException(e);
		} catch (GeneralSecurityException e) {
			throw new DictaoServiceCallerException(e);
		}
	}

	protected final String getClientKeyAlias() {
		return clientKeyAlias;
	}

	public final String getUrl() {
		return url;
	}

}
