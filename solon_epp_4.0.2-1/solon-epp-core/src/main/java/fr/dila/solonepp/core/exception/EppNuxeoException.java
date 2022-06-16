package fr.dila.solonepp.core.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

public class EppNuxeoException extends NuxeoException {
    /**
     *
     */
    private static final long serialVersionUID = 686720821697690825L;

    public EppNuxeoException() {
        super();
    }

    public EppNuxeoException(String message) {
        super(message);
    }

    public EppNuxeoException(String message, NuxeoException cause) {
        super(message, cause);
    }

    public EppNuxeoException(String message, Throwable cause) {
        super(message, cause);
    }

    public EppNuxeoException(Throwable cause) {
        super(cause);
    }

    public EppNuxeoException(NuxeoException cause) {
        super(cause);
    }
}
