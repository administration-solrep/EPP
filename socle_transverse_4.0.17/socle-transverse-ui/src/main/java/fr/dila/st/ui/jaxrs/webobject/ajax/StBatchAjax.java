package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "BatchAjax")
public class StBatchAjax extends SolonWebObject {

    @Path("recherche")
    public Object doBatchRecherche() {
        return newObject("BatchSuiviAjax");
    }

    @Path("notif")
    public Object doBatchNotif() {
        return newObject("BatchNotifAjax");
    }
}
