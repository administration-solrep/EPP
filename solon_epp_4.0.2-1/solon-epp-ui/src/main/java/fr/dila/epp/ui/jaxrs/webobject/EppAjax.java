package fr.dila.epp.ui.jaxrs.webobject;

import fr.dila.st.ui.jaxrs.webobject.StAjax;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliAjax")
public class EppAjax extends StAjax {

    public EppAjax() {
        super();
    }

    @Path("corbeille")
    public Object doSearch() {
        return newObject("EppCorbeilleAjax", context);
    }

    @Path("dossier")
    public Object doDossier() {
        return newObject("EppDossierAjax", context);
    }

    @Path("recherche")
    public Object doRecherche() {
        return newObject("RechercheAjax", context);
    }

    @Path("profile")
    public Object doProfile() {
        return newObject("ProfileAjax", context);
    }

    @Path("communication")
    public Object doCommunication() {
        return newObject("CommunicationAjax", context);
    }
}
