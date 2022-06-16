package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.UtilisateurMenuService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public class UtilisateurMenuServiceImpl extends LeftMenuServiceImpl implements UtilisateurMenuService {

    public UtilisateurMenuServiceImpl() {
        super("UTILISATEUR_MENU");
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        STUserManagerActionsDTO dto = new STUserManagerActionsDTO();
        dto.setIsCurrentUserPermanent(
            STUIServiceLocator.getSTUserManagerUIService().isCurrentUserPermanent(context.getSession().getPrincipal())
        );
        context.putInContextData("reponsesUserManagerActions", dto);
        return super.getData(context);
    }
}
