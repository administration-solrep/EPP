package fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 *
 *
 */
public class FeuilleRouteNotLockedException extends NuxeoException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FeuilleRouteNotLockedException() {}

    public FeuilleRouteNotLockedException(String message) {
        super(message);
    }

    public FeuilleRouteNotLockedException(Throwable th) {
        super(th);
    }

    public FeuilleRouteNotLockedException(String message, Throwable th) {
        super(message, th);
    }
}
