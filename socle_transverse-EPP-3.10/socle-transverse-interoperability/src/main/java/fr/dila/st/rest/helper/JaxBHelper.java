package fr.dila.st.rest.helper;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * A collection of utilities for handling JaxB objects
 * 
 * @author fbarmes
 * 
 */
public class JaxBHelper {

	public static final String	REQUEST	= "request";

	/**
	 * build JaxB Object from a file in the classpath
	 * 
	 * @param <T>
	 * @param filePath
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static <T> T buildRequestFromFile(String filePath, Class<T> clazz) throws JAXBException {

		InputStream iStream = JaxBHelper.class.getClassLoader().getResourceAsStream(filePath);
		assert iStream != null;

		T request = JaxBHelper.unmarshall(iStream, clazz);
		assert request != null;

		return request;
	}

	/**
	 * Transforms an InputStream into the corresponding Jax-B object
	 * 
	 * @param <P>
	 * @param iStream
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static final <P> P unmarshall(InputStream iStream, Class<P> clazz) throws JAXBException {

		JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);
		Unmarshaller unmarshaller = jcb.createUnmarshaller();

		@SuppressWarnings("unchecked")
		P jaxbObject = (P) unmarshaller.unmarshal(iStream);

		return jaxbObject;
	}

	/**
	 * Marshall object to String
	 * 
	 * @param <P>
	 * @param jaxbObject
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static final <P> String marshallToString(P jaxbObject, Class<P> clazz) throws JAXBException {

		JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);

		Marshaller marshaller = jcb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(jaxbObject, writer);

		return writer.toString();
	}

	/**
	 * Marshall object to String
	 * 
	 * @param <P>
	 * @param jaxbObject
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static final <P> String marshallToString(P jaxbObject) throws JAXBException {

		JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(jaxbObject.getClass());
		Marshaller marshaller = jcb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(jaxbObject, writer);

		return writer.toString();
	}

	/**
	 * Marshall to a string the content of a file representing a request or response
	 * 
	 * @param <Q>
	 *            the request type
	 * @param <P>
	 *            the response type
	 * @param which
	 *            "request" or "response"
	 * @param requestFile
	 *            the file to marshall
	 * @param qClazz
	 *            the class of <Q>
	 * @param response
	 *            the reponse object
	 * @param pClazz
	 *            the class of
	 *            <P>
	 * @return
	 * @throws JAXBException
	 */
	public static final <Q, P> String getRequestOrResponseAsString(String which, String requestFile, Class<P> qClazz,
			Q response, Class<Q> pClazz) throws JAXBException {

		String result = null;

		if (which != null && which.equals(REQUEST)) {
			P request = JaxBHelper.buildRequestFromFile(requestFile, qClazz);
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
	 * @param username
	 *            the username of the consumer
	 * @param request
	 *            the request
	 * @param qClazz
	 *            the request class
	 * @param response
	 *            the response
	 * @param rClazz
	 *            the response class
	 * @throws JAXBException
	 */
	public static <Q, R> String logInWsTransaction(String service, String method, String username, Q request,
			Class<Q> qClazz, R response, Class<R> rClazz) throws JAXBException {

		StringBuffer buffer = new StringBuffer();

		buffer.append("---BEGIN IN TRANSACTION service=(" + service + "), method=(" + method + "), username=("
				+ username + ")---\n");

		buffer.append("--REQUEST--\n");
		if (request != null) {
			buffer.append(JaxBHelper.marshallToString(request, qClazz));
		} else {
			buffer.append("null");
		}
		buffer.append("\n");

		buffer.append("--RESPONSE--\n");
		if (response != null) {
			buffer.append(JaxBHelper.marshallToString(response, rClazz));
		} else {
			buffer.append("null");
		}
		buffer.append("\n");

		buffer.append("---END TRANSACTION---");

		return buffer.toString();
	}

	/**
	 * Log an incoming or outgoing web service transaction
	 * 
	 * @param <Q>
	 * @param <R>
	 * @param service
	 * @param username
	 *            the username of the consumer
	 * @param request
	 *            the request
	 * @param qClazz
	 *            the request class
	 * @param response
	 *            the response
	 * @param rClazz
	 *            the response class
	 * @throws JAXBException
	 */
	public static <Q, R> String logOutWsTransaction(String url, String username, String alias, Q request, R response)
			throws JAXBException {

		StringBuffer buffer = new StringBuffer();

		buffer.append("---BEGIN OUT TRANSACTION url=(" + url + "), username=(" + username + "), alias=(" + alias
				+ ")---\n");

		buffer.append("--REQUEST--\n");
		if (request != null) {
			buffer.append(JaxBHelper.marshallToString(request));
		} else {
			buffer.append("null");
		}
		buffer.append("\n");

		buffer.append("--RESPONSE--\n");
		if (response != null) {
			buffer.append(JaxBHelper.marshallToString(response));
		} else {
			buffer.append("null");
		}
		buffer.append("\n");

		buffer.append("---END TRANSACTION---");

		return buffer.toString();
	}

}
