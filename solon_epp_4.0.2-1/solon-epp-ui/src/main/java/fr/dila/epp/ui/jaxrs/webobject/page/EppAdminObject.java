package fr.dila.epp.ui.jaxrs.webobject.page;

import fr.dila.epp.ui.th.model.EppAdminTemplate;
import fr.dila.st.ui.jaxrs.webobject.pages.AdminObject;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliAdmin")
public class EppAdminObject extends AdminObject {

    public EppAdminObject() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new EppAdminTemplate();
    }
}
