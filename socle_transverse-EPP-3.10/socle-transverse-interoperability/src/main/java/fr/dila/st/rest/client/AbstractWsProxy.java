package fr.dila.st.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import fr.dila.st.rest.client.helper.MarshallerFactory;
import fr.dila.st.rest.helper.HttpClientBuilder;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.dila.st.rest.helper.JaxbContextHolder;

/**
 * Abstract web service proxy. Handles all the low level communication stuff for web service communication
 * 
 * @author admin
 * 
 */
public abstract class AbstractWsProxy {

	protected static final Logger	LOGGER	= Logger.getLogger(AbstractWsProxy.class);

	private final String			endpoint;
	private final String			basePath;

	private final String			username;
	private final String			password;
	private final String			keyAlias;

	/**
	 * Constructor
	 * 
	 * @param endpoint
	 * @param basePath
	 * @param username
	 * @param password
	 * @param keyAlias
	 */
	public AbstractWsProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super();
		this.endpoint = endpoint;
		this.basePath = basePath;
		this.username = username;
		this.password = password;
		this.keyAlias = keyAlias;
	}

	/**
	 * @return the service name of the proxy
	 */
	protected abstract String getServiceName();

	protected final String getBasePath() {
		return basePath;
	}

	@SuppressWarnings("unchecked")
	protected <P> P doGet(String methodName, Class<P> responseClazz) throws HttpTransactionException, JAXBException {

		String responseStr = handleHttpGetTransaction(methodName);

		if (responseClazz == String.class) {
			return (P) responseStr;
		}

		if (responseStr == null) {
			throw new JAXBException("Can not marshall null response");
		}

		P response = unmarshall(responseStr, responseClazz);

		return response;
	}

	protected <Q, P> P doPost(String methodName, Q request, Class<P> responseClazz) throws JAXBException,
			HttpTransactionException {

		String requestStr = "";

		if (request != null) {
			requestStr = marshall(request, false);
		} else {
			throw new JAXBException("Can not marshall null request");
		}

		String destination = "";
		try {
			URL url = this.buildServiceUrl(methodName);
			destination = url.toString();
		} catch (MalformedURLException e) {
			destination = methodName;
		}

		LOGGER.debug("Webservice url: " + destination);

		String responseStr;
		try {
			responseStr = handleHttPostTransaction(methodName, requestStr);
		} catch (Exception e) {
			LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, null));
			LOGGER.debug("Erreur sur le flux RESPONSE : " + e.getMessage());
			throw new HttpTransactionException(e);
		}

		if (responseStr != null) {
			P response = unmarshall(responseStr, responseClazz);
			LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, response));
			return response;
		} else {
			LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, null));
			throw new JAXBException("Can not marshall null response");
		}
	}

	/**
	 * Transforms a JAX-B object to String
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	protected <Q> String marshall(Q obj, boolean isFormatted) throws JAXBException {

		MarshallerFactory marshallerFactory = new MarshallerFactory();
		marshallerFactory.setFormattedOutput(true);

		Marshaller m = marshallerFactory.getMarshaller(obj.getClass());

		if (isFormatted) {
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}

		// JAXBContext jcb = JAXBContext.newInstance(obj.getClass());
		// Marshaller m = jcb.createMarshaller();

		StringWriter sw = new StringWriter();
		m.marshal(obj, sw);

		return sw.toString();
	}

	/**
	 * Transforms a String to corresponding JAX-B object
	 * 
	 * @param str
	 *            the string to unmarshall
	 * @param clazz
	 *            the target class
	 * @return an instance of P
	 * @throws JAXBException
	 */
	protected <P> P unmarshall(String str, Class<P> clazz) throws JAXBException {

		JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);

		Unmarshaller u = jcb.createUnmarshaller();

		@SuppressWarnings("unchecked")
		P p = (P) u.unmarshal(new ByteArrayInputStream(str.getBytes()));

		return p;
	}

	private final String handleHttpGetTransaction(String methodeName) throws HttpTransactionException {

		HttpClient httpClient = null;

		try {
			URL serviceUrl = buildServiceUrl(methodeName);
			HttpClientBuilder builder = new HttpClientBuilder(serviceUrl);
			builder.setKeyAlias(this.keyAlias);
			httpClient = builder.build();
			HttpGet httpGet = new HttpGet(serviceUrl.toString());

			if (this.username != null && this.password != null) {
				String encodedCredential = "Basic "
						+ new String(Base64.encodeBase64((username + ":" + password).getBytes()));
				httpGet.addHeader("Authorization", encodedCredential);
			}

			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpTransactionException("Bad response status : " + httpResponse.getStatusLine());
			}

			HttpEntity httpEntity = httpResponse.getEntity();
			String responseBody = EntityUtils.toString(httpEntity);
			return responseBody;
		} catch (MalformedURLException e) {
			LOGGER.debug("URL mal formée : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (GeneralSecurityException e) {
			LOGGER.debug("Exception dans le typage : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (ClientProtocolException e) {
			LOGGER.debug("Exception ClientProtocol : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (IOException e) {
			LOGGER.debug("Exception de type I/O : " + e.getMessage());
			throw new HttpTransactionException(e);
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * HTTPClient facade that handles the transactions
	 * 
	 * @param methodeName
	 * @param request
	 * @return
	 */
	private final String handleHttPostTransaction(String methodeName, String request) throws HttpTransactionException {

		HttpClient httpClient = null;

		try {
			URL serviceUrl = buildServiceUrl(methodeName);
			HttpClientBuilder builder = new HttpClientBuilder(serviceUrl);
			builder.setKeyAlias(this.keyAlias);
			httpClient = builder.build();
			HttpPost httpPost = new HttpPost(serviceUrl.toString());

			HttpEntity httpPostBody = new StringEntity(request, "UTF-8");
			httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");

			if (this.username != null && this.password != null) {
				String encodedCredential = "Basic "
						+ new String(Base64.encodeBase64((username + ":" + password).getBytes()));
				httpPost.addHeader("Authorization", encodedCredential);
			}

			httpPost.setEntity(httpPostBody);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpTransactionException("Bad response status : " + httpResponse.getStatusLine());
			}

			HttpEntity httpEntity = httpResponse.getEntity();
			String responseBody = EntityUtils.toString(httpEntity);

			return responseBody;
		} catch (MalformedURLException e) {
			LOGGER.debug("URL mal formée : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (GeneralSecurityException e) {
			LOGGER.debug("Exception dans le typage : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.debug("Encodage de caractères non supporté  : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (ClientProtocolException e) {
			LOGGER.debug("Exception ClientProtocol : " + e.getMessage());
			throw new HttpTransactionException(e);
		} catch (IOException e) {
			LOGGER.debug("Exception de type I/O : " + e.getMessage());
			throw new HttpTransactionException(e);
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * Build a full url to the service
	 * 
	 * @param methodName
	 * @return
	 * @throws MalformedURLException
	 */
	private URL buildServiceUrl(String methodName) throws MalformedURLException {
		URL serviceUrl = buildServiceUrl(this.endpoint, this.getBasePath(), this.getServiceName(), methodName);
		return serviceUrl;
	}

	/**
	 * Build a full url to the service
	 * 
	 * @param endpoint
	 * @param basePath
	 * @param serviceName
	 * @param methodName
	 * @return
	 * @throws MalformedURLException
	 */
	protected static URL buildServiceUrl(String endpoint, String basePath, String serviceName, String methodName)
			throws MalformedURLException {

		StringBuffer sb = new StringBuffer();

		for (String str : new String[] { endpoint, basePath, serviceName, methodName }) {

			if (str == null || str.length() == 0) {
				continue;
			}

			if (str.startsWith("/")) {
				str = str.substring(1);
			}

			sb.append(str);
			if (!str.endsWith("/")) {
				sb.append("/");
			}

		}

		return new URL(sb.toString());

	}

}
