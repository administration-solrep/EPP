package fr.dila.solonepp.core.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Exception lever une erreur est detectée lors de la creation et l'initialisation d'un evenement successif a partir d'un autre
 * @author asatre
 *
 */
public class InitEvenementException extends NuxeoException {
    private static final long serialVersionUID = -3456381778497691141L;

    public InitEvenementException(String message) {
        super(message);
    }
}
