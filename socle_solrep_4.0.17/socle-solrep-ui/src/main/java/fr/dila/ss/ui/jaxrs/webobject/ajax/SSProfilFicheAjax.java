package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;

import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ProfilFicheAjax")
public class SSProfilFicheAjax extends SolonWebObject {
    public static final int PROFILS_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String FICHE_PROFIL_URL = "/admin/profile/ficheProfil";
    public static final String FICHE_PROFIL_AJAX_URL = "/ajax/profile/ficheProfil";
    public static final String FICHE_PROFIL_LABEL = "Fiche profil";

    public SSProfilFicheAjax() {
        super();
    }

    protected ProfilUIService getProfilUIService() {
        return SSUIServiceLocator.getProfilUIService();
    }

    @GET
    public ThTemplate getFicheProfil(
        @QueryParam("id") String id,
        @QueryParam("fonctionsAttribuees") String fonctionsAttribuees
    ) {
        context.setNavigationContextTitle(
            new Breadcrumb(
                FICHE_PROFIL_LABEL,
                FICHE_PROFIL_URL,
                PROFILS_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );
        context.putInContextData(PROFILE_ID, id);
        context.putInContextData(SORT_ORDER, SortOrder.fromValue(fonctionsAttribuees));
        template.setContext(context);
        Map<String, Object> map = template.getData();

        DocumentModel profilDoc = getProfilUIService().getProfilDoc(context);
        context.setCurrentDocument(profilDoc);

        FicheProfilDTO fiche = getProfilUIService().getFicheProfilDTO(context);

        List<String> fonctionsOfProfil = fiche
            .getFonctions()
            .stream()
            .map(SelectValueDTO::getId)
            .collect(Collectors.toList());

        map.put(SSTemplateConstants.PROFIL, fiche);
        map.put(SSTemplateConstants.FONCTIONS, fiche.getFonctions());
        map.put(SSTemplateConstants.FONCTIONS_OF_PROFIL, fonctionsOfProfil);
        map.put(STTemplateConstants.LST_COLONNES, fiche.getLstColonnes());
        map.put(STTemplateConstants.DATA_URL, FICHE_PROFIL_URL);
        map.put(STTemplateConstants.DATA_AJAX_URL, FICHE_PROFIL_AJAX_URL);
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        map.put(STTemplateConstants.DELETE_ACTION, context.getAction(SSActionEnum.ADMIN_PROFIL_DELETE));
        map.put(STTemplateConstants.EDIT_ACTION, context.getAction(SSActionEnum.ADMIN_PROFIL_EDIT));
        template.setData(map);
        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/supprimer")
    public Response deleteProfil(@FormParam("id") String id) {
        ProfilUIService profilUIService = getProfilUIService();
        context.putInContextData(PROFILE_ID, id);
        DocumentModel profilDoc = profilUIService.getProfilDoc(context);
        context.setCurrentDocument(profilDoc);

        profilUIService.computeProfilActions(context);
        verifyAction(SSActionEnum.ADMIN_PROFIL_DELETE, "/admin/profile/ficheProfil/supprimer");

        profilUIService.deleteProfile(context, id);
        context.getMessageQueue().addSuccessToQueue("Profil supprimé.");

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate(
            "fragments/components/profil/fonctionsAttribueesList",
            getMyContext()
        );
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
