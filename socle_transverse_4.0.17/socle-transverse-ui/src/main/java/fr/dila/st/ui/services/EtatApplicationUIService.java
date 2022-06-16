package fr.dila.st.ui.services;

import fr.dila.st.ui.bean.GestionAccesDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public interface EtatApplicationUIService {
    /**
     * Retreindre l'accès à l'application
     */
    void restrictAccess(SpecificContext context);

    void restoreAccess(SpecificContext context);

    boolean isAccessRestricted(SpecificContext context);

    GestionAccesDTO getEtatApplicationDocument(SpecificContext context);

    Map<String, Object> getEtatApplicationDocumentUnrestricted();

    void updateDocument(SpecificContext context);

    /**
     * Controle l'accès à la vue correspondante
     *
     */
    boolean isAccessAuthorized(SpecificContext context);
}
