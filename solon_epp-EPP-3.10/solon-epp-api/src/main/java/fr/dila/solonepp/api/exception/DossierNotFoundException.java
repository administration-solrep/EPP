package fr.dila.solonepp.api.exception;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Exception lors que le dossier n'est pas trouvé dans les WS epp
 * @author admin
 *
 */
public class DossierNotFoundException extends ClientException {

    
    private static final long serialVersionUID = -296736911106955466L;
    
    public DossierNotFoundException(String message) {
        super(message);
    }

   

}
