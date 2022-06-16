package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.WebEngine;

public interface SharedBetweenAdminAndUser {
    default ThTemplate getMySharedTemplate() {
        if (
            WebEngine.getActiveContext() != null &&
            WebEngine.getActiveContext().getPrincipal() != null &&
            WebEngine.getActiveContext().getPrincipal().isMemberOf(STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER)
        ) {
            return getMyAdminTemplate();
        } else {
            return getMyUserTemplate();
        }
    }

    ThTemplate getMyAdminTemplate();

    ThTemplate getMyUserTemplate();
}
