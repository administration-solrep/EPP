package fr.sword.naiad.commons.xml.jaxb;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;
import fr.sword.naiad.commons.xml.context.JaxbContextHolder;

/**
 * A collection of utilities for handling JaxB objects
 * 
 * @author fbarmes
 * 
 */
public final class JaxBHelper {

	public static final String REQUEST = "request";
	public static final String NULL = "null";
	public static final String NEWLINE = "\n";
	public static final int SB_SIZE = 256;
	
	/**
	 * Default constructor. This class is not to be instanciated
	 */
	private JaxBHelper() {
		super();
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @param filePath
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static <T> T buildRequestFromFile(final String filePath, final Class<T> clazz) throws JAXBException {

		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		Assert.assertNotNull(is);

		final T request = JaxBHelper.unmarshall(is, clazz);
		Assert.assertNotNull(request);

		return request;
	}

	/**
	 * Transforms an InputStream into the corresponding Jax-B object
	 * 
	 * @param <P>
	 * @param is
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static <P> P unmarshall(final InputStream is, final Class<P> clazz) throws JAXBException {

		final JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);
		final Unmarshaller unmarshaller = jcb.createUnmarshaller();

		@SuppressWarnings("unchecked")
		final P jaxbObj = (P) unmarshaller.unmarshal(is);

		return jaxbObj;   
	}

	/**
	 * Marshall object to String
	 * 
	 * @param <P>
	 * @param jaxbObj
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static <P> String marshallToString(final P jaxbObj, final Class<P> clazz) throws JAXBException {

		final JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);
		final Marshaller marshaller = jcb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter swr = new StringWriter();
		marshaller.marshal(jaxbObj, swr);

		return swr.toString();
	}

	/**
	 * Marshall object to String
	 * 
	 * @param <P>
	 * @param jaxbObj
	 * @return
	 * @throws JAXBException
	 */
	public static <P> String marshallToString(final P jaxbObj) throws JAXBException {

		final JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(jaxbObj.getClass());
		final Marshaller marshaller = jcb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter swr = new StringWriter();
		marshaller.marshal(jaxbObj, swr);

		return swr.toString();
	}

	/**
	 * Marshall to a string the content of a file representing a request or response
	 * 
	 * @param <Q> the request type
	 * @param <P> the response type
	 * @param which "request" or "response"
	 * @param requestFile the file to marshall
	 * @param qClazz the class of the request type
	 * @param response the reponse object
	 * @param pClazz the class of the response type
	 * @return
	 * @throws JAXBException
	 */
	public static <Q, P> String getRequestOrResponseAsString(final String which, final String requestFile, final Class<P> qClazz, final Q response, final Class<Q> pClazz)
	    throws JAXBException {

		String result = null;

		if (which != null && which.equals(REQUEST)) {
			final P request = JaxBHelper.buildRequestFromFile(requestFile, qClazz);
			result = JaxBHelper.marshallToString(request, qClazz);

		} else {
			result = JaxBHelper.marshallToString(response, pClazz);
		}

		return result;
	}

	/**
	 * Log an incoming or outgoing web service transaction
	 * 
	 * @param <Q>
	 * @param <R>
	 * @param service
	 * @param username the username of the consumer
	 * @param request the request
	 * @param qClazz the request class
	 * @param response the response
	 * @param rClazz the response class
	 * @throws JAXBException
	 */
	public static <Q, R> String logInWsTransaction(final String service, final String method, final String username, final Q request, final Class<Q> qClazz, final R response,
	    final Class<R> rClazz) throws JAXBException {

		final StringBuffer sbr = new StringBuffer(SB_SIZE);

		sbr.append("---BEGIN IN TRANSACTION service=(" + service + "), method=(" + method + "), username=(" + username + ")---\n");
		sbr.append(logWsTransaction(request, response));
		return sbr.toString();
	}

	/**
	 * Log an incoming or outgoing web service transaction
	 * 
	 * @param <Q>
	 * @param <R>
	 * @param username the username of the consumer
	 * @param request the request
	 * @param response the response
	 * @throws JAXBException
	 */
	public static <Q, R> String logOutWsTransaction(final String url, final String username, final String alias, final Q request, final R response) throws JAXBException {

		final StringBuffer sbr = new StringBuffer(SB_SIZE);

		sbr.append("---BEGIN OUT TRANSACTION url=(" + url + "), username=(" + username + "), alias=(" + alias + ")---\n");
		sbr.append(logWsTransaction(request, response));
		return sbr.toString();
	}

	/**
	 * generate a log String
	 * @param request
	 * @param response
	 * @return
	 * @throws JAXBException
	 */
	private static <Q, R> String logWsTransaction(final Q request, final R response) throws JAXBException {

		final StringBuffer sbr = new StringBuffer(SB_SIZE);

		sbr.append("--REQUEST--\n");
		if (request == null) {
			sbr.append(NULL);			
		} else {
			sbr.append(JaxBHelper.marshallToString(request));
		}
		sbr.append(NEWLINE);

		sbr.append("--RESPONSE--\n");
		if (response == null) {
			sbr.append(NULL);
		} else {
			sbr.append(JaxBHelper.marshallToString(response));
		}
		sbr.append(NEWLINE);

		sbr.append("---END TRANSACTION---");

		return sbr.toString();
	}
	
	
}
