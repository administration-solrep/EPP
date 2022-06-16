package fr.dila.epp.ui.jaxrs.webobject.page.admin;

import fr.dila.epp.ui.th.model.EppAdminTemplate;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliOrganigramme")
public class EppOrganigramme extends STOrganigramme {

    public EppOrganigramme() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate(SpecificContext context) {
        return new EppAdminTemplate();
    }
}
