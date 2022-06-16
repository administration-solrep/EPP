package fr.dila.st.ui.jaxrs.webobject;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliLogout")
public class LogoutObject extends SolonWebObject {

    @GET
    public Object doLogout() {
        return redirectWithoutContext("/logout");
    }
}
