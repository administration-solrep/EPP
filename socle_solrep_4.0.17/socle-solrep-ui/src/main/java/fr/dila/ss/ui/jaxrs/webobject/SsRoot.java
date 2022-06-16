package fr.dila.ss.ui.jaxrs.webobject;

import fr.dila.st.ui.jaxrs.webobject.StRoot;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Path("ss")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "ss")
public class SsRoot extends StRoot {

    public SsRoot() {
        super();
    }

    @Path("historiqueActualites")
    public Object doHistoriqueActualites() {
        return newObject("AppliHistoriqueActualites");
    }
}
