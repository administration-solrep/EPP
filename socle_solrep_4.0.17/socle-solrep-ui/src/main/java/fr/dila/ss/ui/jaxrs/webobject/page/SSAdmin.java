package fr.dila.ss.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;

import fr.dila.st.ui.jaxrs.webobject.pages.AdminObject;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliAdmin")
public class SSAdmin extends AdminObject {

    public SSAdmin() {
        super();
    }

    @Path("fdr")
    public Object getModeleFeuilleRoute() {
        context.putInContextData(BREADCRUMB_BASE_URL, "/admin/fdr");
        return newObject("ModeleFeuilleRoute", context, template);
    }

    @Path("actualites")
    public Object getActualites() {
        return newObject("Actualites", context, template);
    }

    @Path("profile")
    public Object getProfils() {
        return newObject("Profils", context, template);
    }

    @Path("historique")
    public Object getHistoriqueMigrations() {
        return newObject("HistoriqueMigration", context, template);
    }

    @Path("param")
    public Object getParametres() {
        return newObject("Parametres", context, template);
    }

    @Path("journal")
    public Object getJournal() {
        return newObject("JournalTechnique", context, template);
    }

    @Path("supervision")
    public Object getSupervision() {
        return newObject("Supervision", context, template);
    }

    @Path("migration")
    public Object getMigration() {
        return newObject("Migration", context);
    }
}
