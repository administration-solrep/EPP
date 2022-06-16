package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import fr.dila.epp.ui.bean.DetailDossier;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EppDossierDetailDossier")
public class EppDossierDetailDossier extends SolonWebObject {

    public EppDossierDetailDossier() {
        super();
    }

    @GET
    public ThTemplate getDetail(@PathParam("id") String id, @PathParam("tab") String tab) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        DetailDossier detail = SolonEppUIServiceLocator.getMetadonneesUIService().getDetailDossier(context);

        template.getData().put(STTemplateConstants.LST_WIDGETS, detail.getLstWidgets());
        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/detailDossier", getMyContext());
    }
}
