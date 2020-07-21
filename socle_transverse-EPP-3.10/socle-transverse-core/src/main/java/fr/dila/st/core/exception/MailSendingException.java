package fr.dila.st.core.exception;

import org.nuxeo.ecm.core.api.ClientException;

public class MailSendingException extends STException {

	private static final long	serialVersionUID	= -3721621140180452546L;

	public MailSendingException() {
		super();
	}

	public MailSendingException(ClientException cause) {
		super(cause);
	}

	public MailSendingException(String message, ClientException cause) {
		super(message, cause);
	}

	public MailSendingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MailSendingException(String message) {
		super(message);
	}

	public MailSendingException(Throwable cause) {
		super(cause);
	}

}
