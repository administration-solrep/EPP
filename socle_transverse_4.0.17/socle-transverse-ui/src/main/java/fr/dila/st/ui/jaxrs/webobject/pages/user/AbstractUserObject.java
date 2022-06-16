package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;

public abstract class AbstractUserObject extends SolonWebObject {

    protected void init(String userId) {
        context.putInContextData(USER_ID, userId);
        STUserManagerUIService umActionService = STUIServiceLocator.getSTUserManagerUIService();
        umActionService.initUserContext(context);
    }
}
