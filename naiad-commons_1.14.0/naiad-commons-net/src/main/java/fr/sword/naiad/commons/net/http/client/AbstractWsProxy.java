package fr.sword.naiad.commons.net.http.client;

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

import fr.sword.naiad.commons.xml.context.JaxbContextHolder;
import fr.sword.naiad.commons.xml.jaxb.JaxBHelper;
import fr.sword.naiad.commons.xml.jaxb.MarshallerFactory;

/**
 * Abstract web service proxy.
 * Handles all the low level HTTP (GET or POST) communications for web service communication
 * 
 * @author fbarmes
 * 
 */
public abstract class AbstractWsProxy {

	protected static final Logger LOGGER = Logger.getLogger(AbstractWsProxy.class);

	private static final String FORWARD_SLASH = "/";
	
	private final String endpoint;
	private final String username;
	private final String password;
	private final String keyAlias;

	/**
	 * Constructor
	 * 
	 * @param endpoint
	 * @param username
	 * @param password
	 * @param keyAlias
	 */
	public AbstractWsProxy(final String endpoint, final String username, final String password, final String keyAlias) {
		super();
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
		this.keyAlias = keyAlias;
	}

	/**
	 * @return the service name of the proxy
	 */
	protected abstract String getServiceName();

	@SuppressWarnings("unchecked")
	protected final <P> P doGet(final String methodName, final Class<P> responseClazz) throws HttpTransactionException, JAXBException {

		final String responseStr = handleHttpGetTransaction(methodName);

		if (responseClazz == String.class) {
			return (P) responseStr;
		}

		if (responseStr == null) {
			throw new JAXBException("Can not marshall null response");
		}

		final P response = unmarshall(responseStr, responseClazz);

		return response;
	}

	protected final <Q, P> P doPost(final String methodName, final Q request, final Class<P> responseClazz) throws JAXBException, HttpTransactionException {

		if (request == null) {
			throw new JAXBException("Can not marshall null request");
		} 
		
		String requestStr = marshall(request, false);
		
		String destination = "";
		try {
			final URL url = buildServiceUrl(methodName);
			destination = url.toString();
		} catch (final MalformedURLException e) {
			destination = methodName;
		}

		String responseStr;
		try {
			responseStr = handleHttPostTransaction(methodName, requestStr);
		} catch (final Exception e) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, null));
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RESPONSE: Exception " + e.getMessage(), e);
			}
			throw new HttpTransactionException(e);
		}
		
		if (responseStr == null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, null));
			}
			throw new JAXBException("Can not marshall null response");
		} 
		
		final P response = unmarshall(responseStr, responseClazz);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(JaxBHelper.logOutWsTransaction(destination, this.username, this.keyAlias, request, response));
		}
		return response;
	}

	/**
	 * Transforms a JAX-B object to String
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	protected <Q> String marshall(final Q obj, final boolean isFormatted) throws JAXBException {

		final MarshallerFactory marshallerFactory = new MarshallerFactory();
		marshallerFactory.setFormattedOutput(true);

		final Marshaller marshaller = marshallerFactory.getMarshaller(obj.getClass());

		if (isFormatted) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}

		//		JAXBContext jcb = JAXBContext.newInstance(obj.getClass());
		//		Marshaller m = jcb.createMarshaller();		

		final StringWriter swr = new StringWriter();
		marshaller.marshal(obj, swr);

		return swr.toString();
	}

	/**
	 * Transforms a String to corresponding JAX-B object
	 * 
	 * @param str the string to unmarshall
	 * @param clazz the target class
	 * @return an instance of P
	 * @throws JAXBException
	 */
	protected <P> P unmarshall(final String str, final Class<P> clazz) throws JAXBException {

		final JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);

		final Unmarshaller unmarshaller = jcb.createUnmarshaller();

		@SuppressWarnings("unchecked")
		final P result = (P) unmarshaller.unmarshal(new ByteArrayInputStream(str.getBytes()));

		return result;
	}

	private String handleHttpGetTransaction(final String methodeName) throws HttpTransactionException {

		HttpClient httpClient = null;
		try {
			final URL serviceUrl = buildServiceUrl(methodeName);

			final HttpClientBuilder builder = new HttpClientBuilder(serviceUrl);

			builder.setKeyAlias(this.keyAlias);

			if (this.username != null && this.password != null) {
				builder.setCredentials(this.username, this.password);
			}
			httpClient = builder.build();

			final HttpGet httpGet = new HttpGet(serviceUrl.toString());

			final HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpTransactionException("Bad response status : " + httpResponse.getStatusLine());
			}

			final HttpEntity httpEntity = httpResponse.getEntity();
			final String responseBody = EntityUtils.toString(httpEntity);

			return responseBody;
		} catch (final GeneralSecurityException e) {
			throw new HttpTransactionException(e);
		} catch (final IOException e) {
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
	private String handleHttPostTransaction(final String methodeName, final String request) throws HttpTransactionException {

		try {

			final URL serviceUrl = buildServiceUrl(methodeName);

			final HttpClientBuilder builder = new HttpClientBuilder(serviceUrl);

			builder.setKeyAlias(this.keyAlias);

			if (this.username != null && this.password != null) {
				builder.setCredentials(this.username, this.password);
			}

			final HttpClient httpClient = builder.build();

			final HttpPost httpPost = new HttpPost(serviceUrl.toString());

			final HttpEntity httpPostBody = new StringEntity(request, "UTF-8");
			httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");

			httpPost.setEntity(httpPostBody);

			final HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpTransactionException("Bad response status : " + httpResponse.getStatusLine());
			}

			final HttpEntity httpEntity = httpResponse.getEntity();
			final String responseBody = EntityUtils.toString(httpEntity);

			return responseBody;

		} catch (final MalformedURLException e) {
			throw new HttpTransactionException(e);
		} catch (final GeneralSecurityException e) {
			throw new HttpTransactionException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new HttpTransactionException(e);
		} catch (final ClientProtocolException e) {
			throw new HttpTransactionException(e);
		} catch (final IOException e) {
			throw new HttpTransactionException(e);
		}

	}
	    
	/**
	 * Build a full url to the service
	 * 
	 * @param methodName
	 * @return
	 * @throws MalformedURLException
	 */
	protected URL buildServiceUrl(final String methodName) throws MalformedURLException {
		return new URL(this.endpoint);
	}

	/**
	 * Build a full url to the service
	 * 
	 * @param endpoint
	 * @param serviceName
	 * @param methodName
	 * @return
	 * @throws MalformedURLException
	 */
	protected static URL buildServiceUrl(final String endpoint, final String serviceName, final String methodName) throws MalformedURLException {

		final StringBuffer sbf = new StringBuffer();

		for (String str : new String[] { endpoint, serviceName, methodName }) {

			if (str == null || str.length() == 0) {
				continue;
			}

			if (str.startsWith(FORWARD_SLASH)) {
				str = str.substring(1);
			}

			sbf.append(str);
			if (!str.endsWith(FORWARD_SLASH)) {
				sbf.append(FORWARD_SLASH);
			}

		}

		return new URL(sbf.toString());

	}

}
