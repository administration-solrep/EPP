package fr.dila.st.ui.jaxrs.webobject.pages;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliAdmin")
public class AdminObject extends SolonWebObject {

    protected STUserManagerUIService getSTUserManagerActionService() {
        return STUIServiceLocator.getSTUserManagerUIService();
    }

    @GET
    public ThTemplate doHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/adminHome");
        context.removeNavigationContextTitle();
        template.setContext(context);
        return template;
    }

    @Path("users")
    public Object getUsers() {
        ThTemplate template = new STUtilisateurTemplate();

        if (context.getWebcontext().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            template = new STAdminTemplate();
        }
        getSTUserManagerActionService().initUserCreationContext(context);
        template.setName("pages/admin/user/listeUsers");
        template.getData().put("newUserAction", context.getAction(STActionEnum.ADMIN_USER_NEW_USER));
        return newObject("SocleUsersAjax", context, template);
    }

    @Path("user")
    public Object getUser() throws IllegalAccessException, InstantiationException {
        context.putInContextData(BREADCRUMB_BASE_URL, "/admin/user");
        context.putInContextData(BREADCRUMB_BASE_LEVEL, Breadcrumb.TITLE_ORDER);
        @SuppressWarnings("unchecked")
        Class<ThTemplate> oldTemplate = (Class<ThTemplate>) context
            .getWebcontext()
            .getUserSession()
            .get(SpecificContext.LAST_TEMPLATE);
        if (oldTemplate != null && oldTemplate != ThTemplate.class) {
            template = oldTemplate.newInstance();
        } else if (
            !context.getWebcontext().getPrincipal().isMemberOf(STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER)
        ) {
            template = new STUtilisateurTemplate();
        }
        return newObject("TransverseUser", context, template);
    }

    @Path("organigramme")
    public Object getOrganigramme() {
        return newObject("AppliOrganigramme", context, template);
    }

    @Path("batch")
    public Object getBatch() {
        return newObject("Batch", context, template);
    }

    @Path("user/acces")
    public Object getAcces() {
        return newObject("Acces", context, template);
    }
}
