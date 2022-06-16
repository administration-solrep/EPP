package fr.dila.ss.ui.jaxrs.webobject.page.dossier;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierFdr")
public class SSDossierFdr extends SolonWebObject {

    public SSDossierFdr() {
        super();
    }

    @GET
    public ThTemplate getFdr() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        String idDossier = context.getCurrentDocument().getId();
        FdrDTO dto = SSUIServiceLocator.getSSFdrUIService().getFeuilleDeRoute(context);
        template.getData().put("dto", dto);

        // type d'étape (pour modal ajout étape)
        template.getData().put(SSTemplateConstants.TYPE_ETAPE, getTypeEtapeAjout());
        template.getData().put(STTemplateConstants.ID_DOSSIER, idDossier);
        template.getData().put(SSTemplateConstants.PROFIL, context.getWebcontext().getPrincipal().getGroups());
        template.setContext(context);

        return template;
    }

    protected List<SelectValueDTO> getTypeEtapeAjout() {
        // Méthode surchargé pour pouvoir filtrer des type d'étape lors de l'ajout/modification
        return SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveFdr() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/fdr", getMyContext());
    }
}
