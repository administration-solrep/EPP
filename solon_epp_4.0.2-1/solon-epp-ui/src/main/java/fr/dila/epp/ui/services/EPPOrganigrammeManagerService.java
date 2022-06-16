package fr.dila.epp.ui.services;

import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface EPPOrganigrammeManagerService extends STOrganigrammeManagerService {
    /**
     * Charge l'arbre contenant l'organigramme
     * @param loadActions       charge-t-on les actions ?
     * @param context           SpecificContext
     * @return
     */
    List<OrganigrammeElementDTO> getOrganigramme(boolean loadActions, SpecificContext context);
}
