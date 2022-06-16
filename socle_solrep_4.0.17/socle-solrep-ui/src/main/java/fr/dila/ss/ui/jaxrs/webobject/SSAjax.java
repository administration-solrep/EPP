package fr.dila.ss.ui.jaxrs.webobject;

import fr.dila.st.ui.jaxrs.webobject.StAjax;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliAjax")
public class SSAjax extends StAjax {

    public SSAjax() {
        super();
    }

    @Path("journal")
    public Object doJournal() {
        return newObject("JournalAjax");
    }

    @Path("historique")
    public Object doHistorique() {
        return newObject("HistoriqueMigrationAjax");
    }

    @Path("etape")
    public Object doEtape() {
        return newObject("AjaxEtape");
    }

    @Path("admin/actualites")
    public Object doActualites() {
        return newObject("AjaxActualites");
    }

    @Path("historiqueActualites")
    public Object doHistoriqueActualites() {
        return newObject("AjaxHistoriqueActualites");
    }

    @Path("admin/journalTechnique")
    public Object doJournalTechnique() {
        return newObject("JournalTechniqueAjax");
    }

    @Path("admin/param")
    public Object doParametres() {
        return newObject("ParametresAjax");
    }

    @Path("actualites")
    public Object doReadActualite() {
        return newObject("AjaxActualites");
    }

    @Path("admin/supervision")
    public Object doSupervision() {
        return newObject("AjaxSupervision");
    }

    @Path("suivi/alertes")
    public Object doAlertes() {
        return newObject("AjaxAlertes");
    }

    @Path("actionDossier")
    public Object doDossiers() {
        return newObject("ActionDossierAjax");
    }

    @Path("fdr")
    public Object doFDR() {
        return newObject("ModeleFeuilleRouteAjax");
    }

    @Path("profile")
    public Object doProfile() {
        return newObject("ProfileAjax");
    }

    @Path("corbeille")
    public Object doCorbeille() {
        return newObject("CorbeilleAjax");
    }

    @Path("migrations")
    public Object doMigrations() {
        return newObject("MigrationAjax");
    }

    @Path("stats")
    public Object doStatistiques() {
        return newObject("StatistiquesAjax");
    }
}
