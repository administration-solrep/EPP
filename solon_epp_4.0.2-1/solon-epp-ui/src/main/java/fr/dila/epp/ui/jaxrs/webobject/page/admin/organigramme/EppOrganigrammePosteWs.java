package fr.dila.epp.ui.jaxrs.webobject.page.admin.organigramme;

import fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme.STOrganigrammePosteWs;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammePosteWs")
public class EppOrganigrammePosteWs extends STOrganigrammePosteWs implements EppSharedBetweenAdminAndUser {

    public EppOrganigrammePosteWs() {
        super();
    }

    @Override
    public ThTemplate getPosteWsCreation(String idParent) {
        ThTemplate template = super.getPosteWsCreation(idParent);
        template.getData().put("hasInstitution", true);

        return template;
    }

    @Override
    public ThTemplate getPosteWsModification(String id) {
        ThTemplate template = super.getPosteWsModification(id);
        template.getData().put("hasInstitution", true);

        return template;
    }
}
