package fr.dila.ss.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

public class SSException extends NuxeoException {
    /**
     *
     */
    private static final long serialVersionUID = -4025730491287895725L;

    public SSException(String message) {
        super(message);
    }

    public SSException(String message, Throwable exc) {
        super(message, exc);
    }

    public SSException(String message, NuxeoException cause) {
        super(message, cause);
    }

    public SSException(Throwable cause) {
        super(cause);
    }

    public SSException(NuxeoException cause) {
        super(cause);
    }
}
