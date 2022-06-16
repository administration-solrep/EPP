package fr.dila.epp.ui.jaxrs.webobject.page;

import static fr.dila.epp.ui.services.impl.EppCorbeilleMenuServiceImpl.ACTIVE_KEY;

import fr.dila.epp.ui.th.model.EppCorbeilleTemplate;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliCorbeille")
public class EppCorbeilleObject extends SolonWebObject {

    @GET
    public ThTemplate doHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/espaceTravailHome");
        context.removeNavigationContextTitle();
        template.setContext(context);
        context.getContextData().put(STTemplateConstants.IS_FROM_TRAVAIL, true);

        // Sur le home on désactive le surlignement de la corbeille sélectionnée
        UserSessionHelper.putUserSessionParameter(context, ACTIVE_KEY, "");

        return template;
    }

    @Path("consulter")
    public Object getCommunications() {
        context.getContextData().put(STTemplateConstants.IS_FROM_TRAVAIL, true);

        ThTemplate template = getMyTemplate();

        template.setName("pages/communication/liste");
        template.setData(new HashMap<>());
        return newObject("EppCorbeilleAjax", context, template);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new EppCorbeilleTemplate();
    }
}
