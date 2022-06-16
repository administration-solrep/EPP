package fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 *
 */
public class FeuilleRouteNotFoundException extends NuxeoException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FeuilleRouteNotFoundException() {}

    public FeuilleRouteNotFoundException(String message) {
        super(message);
    }

    public FeuilleRouteNotFoundException(Throwable th) {
        super(th);
    }

    public FeuilleRouteNotFoundException(String message, Throwable th) {
        super(message, th);
    }
}
