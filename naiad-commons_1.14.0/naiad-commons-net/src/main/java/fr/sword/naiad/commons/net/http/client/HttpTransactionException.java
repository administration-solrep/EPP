package fr.sword.naiad.commons.net.http.client;

/**
 * Exception for handling errors during HTTP transaction
 * @author fbarmes
 *
 */
public class HttpTransactionException extends Exception {

	private static final long serialVersionUID = 1L;

	public HttpTransactionException() {
		super();
	}

	public HttpTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpTransactionException(String message) {
		super(message);
	}

	public HttpTransactionException(Throwable cause) {
		super(cause);
	}
	
	
	
}
