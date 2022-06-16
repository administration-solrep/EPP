package fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 *
 */
public class FeuilleRouteAlreadyLockedException extends NuxeoException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FeuilleRouteAlreadyLockedException() {}

    public FeuilleRouteAlreadyLockedException(String message) {
        super(message);
    }

    public FeuilleRouteAlreadyLockedException(Throwable th) {
        super(th);
    }

    public FeuilleRouteAlreadyLockedException(String message, Throwable th) {
        super(message, th);
    }
}
