package fr.dila.st.ui.jaxrs.webobject;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.core.auth.SolonWebengineFormAuth;
import fr.dila.st.ui.th.model.LayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@Path("st")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "st")
public class StRoot extends SolonModuleRoot {

    public StRoot() {
        super();
    }

    @Path(URLUtils.AJAX_PATH)
    public Object doProcessAjaxRequest() {
        return newObject("AppliAjax");
    }

    @GET
    public Object doHome() {
        return redirect("/home");
    }

    @Path("reinitMdp")
    public Object getResetPassword() {
        return newObject("ResetPwdObject");
    }

    @Path("login")
    public Object doLogin() {
        return newObject("AppliLogin");
    }

    @Path("logout")
    public Object doLogout() {
        return newObject("AppliLogout");
    }

    @Path("{pageTask}")
    public Object doHome(@PathParam("pageTask") String pageTask) {
        String name = pageTask.toLowerCase();
        return newObject("Appli" + StringUtils.capitalize(name));
    }

    @POST
    @Path("/@@login")
    public Object execLogin() {
        return redirect("/home");
    }

    @Path(SolonWebengineFormAuth.EDITION_AUTH_LINK)
    @GET
    public ThTemplate renewToken() {
        SpecificContext context = new SpecificContext();
        ThTemplate template = getMyOwnTemplate("/pages/ngedit/token", context);
        template
            .getData()
            .put("token", STServiceLocator.getTokenService().acquireToken(context.getSession().getPrincipal(), ""));
        return template;
    }

    protected ThTemplate getMyOwnTemplate(String page, SpecificContext context) {
        return new LayoutThTemplate(page, context);
    }
}
