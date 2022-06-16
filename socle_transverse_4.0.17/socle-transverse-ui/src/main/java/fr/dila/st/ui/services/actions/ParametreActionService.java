package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ParametreActionService {
    /**
     * Sauvegarde les changements d'un paramètre.
     */
    void updateDocument(SpecificContext context, DocumentModel doc);

    /**
     * Retourne la valeur d'un paramètre s'il existe
     */
    String getParameterValue(CoreSession session, String parameter);

    String getIdentificationPlateformeLibelle(SpecificContext context);

    String getIdentificationPlateformeCouleur(SpecificContext context);
}
