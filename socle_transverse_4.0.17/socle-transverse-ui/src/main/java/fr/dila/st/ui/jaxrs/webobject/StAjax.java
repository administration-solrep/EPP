package fr.dila.st.ui.jaxrs.webobject;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliAjax")
public class StAjax extends SolonWebObject {

    public StAjax() {
        super();
    }

    @Path("admin/users")
    public Object doUsers() {
        return newObject("SocleUsersAjax");
    }

    @Path("admin/user")
    public Object doUser() {
        return newObject("TransverseUserAjax");
    }

    @Path("rechercher")
    public Object doRechercheUser() {
        return newObject("RechercheUtilisateur");
    }

    @Path("organigramme")
    public Object doOrganigramme() {
        return newObject("OrganigrammeAjax");
    }

    @Path("upload")
    public Object doUpload() {
        return newObject("UploadAjax");
    }

    @Path("admin/batch")
    public Object doBatch() {
        return newObject("BatchAjax");
    }

    @Path("email")
    public Object doMail() {
        return newObject("MailAjax");
    }

    @Path("admin/user/acces")
    public Object doGestionAcces() {
        return newObject("AccesAjax");
    }

    @Path("/exportMail")
    public Object doExportMail() {
        return newObject("ExportMailAjax");
    }

    @Path("/tri-multiple")
    public Object doMultipleSort() {
        return newObject("MultipleSortAjax");
    }

    @Path("recherche")
    public Object doRecherche() {
        return newObject("RechercheAjax");
    }

    @Path("session")
    public Object doUpdateSession() {
        return newObject("SessionAjax");
    }

    @Path("notification")
    public Object notification() {
        return newObject("NotificationAjax");
    }
}
