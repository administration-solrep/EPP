package fr.dila.st.core.exception;

public class MailSendingException extends STException {
    private static final long serialVersionUID = -3721621140180452546L;

    public MailSendingException() {
        super();
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
