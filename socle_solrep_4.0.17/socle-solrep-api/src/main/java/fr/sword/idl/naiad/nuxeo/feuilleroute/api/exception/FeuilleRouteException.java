package fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

public class FeuilleRouteException extends NuxeoException {
    private static final long serialVersionUID = 1L;

    public FeuilleRouteException() {}

    public FeuilleRouteException(String message) {
        super(message);
    }

    public FeuilleRouteException(Throwable th) {
        super(th);
    }

    public FeuilleRouteException(String message, Throwable th) {
        super(message, th);
    }
}
