package fr.sword.naiad.commons.net.http.client;


/**
 * Exception for error in the WS proxy layer
 * @author fbarmes
 *
 */
public class WSProxyFactoryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public WSProxyFactoryException() {
		super();
	}

	public WSProxyFactoryException(String message) {
		super(message);
	}

	public WSProxyFactoryException(Throwable cause) {
		super(cause);
	}

	public WSProxyFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
