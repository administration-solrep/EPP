package fr.dila.st.core.exception;

import org.nuxeo.ecm.core.api.ClientException;

public class STException extends ClientException {

	/**
	 * Serial version UID
	 */
	private static final long	serialVersionUID	= 697766197240243865L;

	public STException() {
		super();
	}

	public STException(String message, Throwable cause) {
		super(message, cause);
	}

	public STException(String message) {
		super(message);
	}

	public STException(Throwable cause) {
		super(cause);
	}
}
