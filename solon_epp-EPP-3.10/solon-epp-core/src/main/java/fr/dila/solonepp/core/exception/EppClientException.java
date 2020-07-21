package fr.dila.solonepp.core.exception;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.WrappedException;

public class EppClientException extends ClientException {

    /**
     * 
     */
    private static final long serialVersionUID = 686720821697690825L;

    public EppClientException() {
    	super();
    }

    public EppClientException(String message) {
        super(message);
    }

    public EppClientException(String message, ClientException cause) {
        super(message, cause);
    }

    public EppClientException(String message, Throwable cause) {
        super(message, WrappedException.wrap(cause));
    }

    public EppClientException(Throwable cause) {
        super(WrappedException.wrap(cause));
    }

    public EppClientException(ClientException cause) {
        super(cause);
    }
    
}
