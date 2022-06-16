package fr.dila.epp.ui.jaxrs.webobject;

import fr.dila.st.ui.jaxrs.webobject.StRoot;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Path("app-ui")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "epp-ui")
public class EppRoot extends StRoot {

    public EppRoot() {
        super();
    }

    @POST
    @Path("/@@login")
    @Override
    public Object execLogin() {
        return redirect("/corbeille");
    }

    @GET
    @Override
    public Object doHome() {
        return redirect("/corbeille");
    }

    @Path("searchEvent.faces")
    public Object searchEventFaces() {
        return newObject("Evenement", new SpecificContext());
    }
}
