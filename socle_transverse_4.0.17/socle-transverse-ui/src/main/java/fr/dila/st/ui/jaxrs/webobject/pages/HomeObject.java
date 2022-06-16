package fr.dila.st.ui.jaxrs.webobject.pages;

import fr.dila.st.ui.core.auth.SolonWebengineFormAuth;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.LayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliHome")
public class HomeObject extends SolonWebObject {
    @Context
    protected HttpServletRequest request;

    @GET
    public Object doHome() {
        SpecificContext myContext = new SpecificContext();
        Map<String, Object> data = new HashMap<>();
        myContext.setContextData(data);
        myContext.setCopyDataToResponse(true);
        return new LayoutThTemplate("pages/home", myContext);
    }

    public Response redirectToEditAuthIfNeeded() {
        HttpSession session = request.getSession();
        if (BooleanUtils.isTrue((Boolean) session.getAttribute(SolonWebengineFormAuth.IS_EDIT_AUTH_KEY))) {
            //Si on venait de l'authent pour l'outil d'édition on redirige vers la bonne page
            return redirect(SolonWebengineFormAuth.EDITION_AUTH_LINK);
        }
        return null;
    }
}
