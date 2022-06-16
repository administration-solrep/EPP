package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliSuivi")
public class SSSuivi extends SolonWebObject {

    public SSSuivi() {
        super();
    }

    @Path("/alertes")
    public Object getAlertes() {
        template.setName("pages/suivi/alerte/alerte");
        return newObject("SuiviAlerte", context, template);
    }
}
