package fr.dila.st.core.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

public class STException extends NuxeoException {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 697766197240243865L;

    public STException() {
        super();
    }

    public STException(String message, Throwable cause) {
        super(message, cause);
    }

    public STException(String message) {
        super(message);
    }

    public STException(String message, Throwable cause, int statusCode) {
        super(message, cause, statusCode);
    }

    public STException(String message, int status) {
        super(message, status);
    }

    public STException(Throwable cause) {
        super(cause);
    }
}
