package fr.dila.ss.ui.services.actions;

import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Action service permettant de gérer les recherches de feuilles de route.
 *
 */
public interface SSRechercheModeleFeuilleRouteActionService {
    ModeleFDRList getModeles(SpecificContext context);

    FeuilleRouteDTO getFeuilleRouteDTOFromDoc(DocumentModel doc, CoreSession session);
}
