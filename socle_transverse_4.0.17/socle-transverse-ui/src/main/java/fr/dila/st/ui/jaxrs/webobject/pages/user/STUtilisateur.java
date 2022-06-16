package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.LayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliUtilisateurs")
public class STUtilisateur extends SolonWebObject {

    public STUtilisateur() {
        super();
    }

    @GET
    public ThTemplate doHome() {
        template.setName("pages/utilisateurs");
        template.setContext(getMyContext());
        return template;
    }

    @Path("compte")
    public Object getCompte() {
        template = new LayoutThTemplate();
        template.getData().put(STTemplateConstants.CANCEL_BUTTON_SKIPPED, true);
        context.putInContextData(BREADCRUMB_BASE_URL, "/utilisateurs/compte");
        context.putInContextData(BREADCRUMB_BASE_LEVEL, 0);
        return newObject("TransverseUser", context, template);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ThTemplate();
    }
}
