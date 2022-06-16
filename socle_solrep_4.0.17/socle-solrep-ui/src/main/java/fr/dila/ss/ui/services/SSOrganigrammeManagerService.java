package fr.dila.ss.ui.services;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;

public interface SSOrganigrammeManagerService extends STOrganigrammeManagerService {
    /**
     * Charge l'arbre contenant l'organigramme
     * @param loadActions       charge-t-on les actions ?
     * @param context           SpecificContext
     * @return
     */
    List<OrganigrammeElementDTO> getOrganigramme(boolean loadActions, SpecificContext context);

    /**
     * Spécifie si l'utilisateur a le droit de modifier l'organigramme ou le
     * ministère concerné le cas échéant.
     *
     * @param session
     * @param ministereId id du ministère
     * @return
     */
    boolean isAllowUpdateOrganigramme(CoreSession session, String ministereId);

    List<Action> loadBaseAdminOrganigrammeActions(CoreSession session);

    List<Action> loadMainAdminOrganigrammeActions(CoreSession session);

    boolean isNodeSelectedForCopy();

    OrganigrammeNode getCopiedOrganigrammeNode();
}
