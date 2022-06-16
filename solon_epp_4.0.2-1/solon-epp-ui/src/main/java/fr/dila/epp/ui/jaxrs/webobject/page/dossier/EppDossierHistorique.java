package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EppDossierHistorique")
public class EppDossierHistorique extends SolonWebObject {

    public EppDossierHistorique() {
        super();
    }

    @GET
    public ThTemplate getHistorique(@PathParam("id") String id, @PathParam("tab") String tab) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        DossierHistoriqueEPP historique = SolonEppUIServiceLocator
            .getHistoriqueDossierUIService()
            .getHistoriqueDossier(context);

        template.getData().put("lstVersions", historique.getLstVersions());
        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/historique", getMyContext());
    }
}
