package fr.dila.epp.ui.jaxrs.webobject.page.admin.organigramme;

import fr.dila.epp.ui.th.model.EppAdminTemplate;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme.SharedBetweenAdminAndUser;
import fr.dila.st.ui.th.model.ThTemplate;

public interface EppSharedBetweenAdminAndUser extends SharedBetweenAdminAndUser {
    @Override
    default ThTemplate getMySharedTemplate() {
        // Pas de partage de template
        return new EppAdminTemplate();
    }
}
