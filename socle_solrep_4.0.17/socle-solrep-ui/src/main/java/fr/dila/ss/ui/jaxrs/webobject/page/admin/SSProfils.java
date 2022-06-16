package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;

import com.google.common.collect.Lists;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.FicheProfilForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Profils")
public class SSProfils extends SolonWebObject {
    public static final int PROFILS_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String DATA_URL = "/admin/profile/liste";
    public static final String DATA_AJAX_URL = "/ajax/profile/listeProfile";
    public static final String NAVIGATION_TITLE = "Gestion des profils";
    private static final String NOUVEAU_PROFIL = "Ajouter un profil";
    private static final String MODIF_PROFIL = "Modification du profil";
    private static final String ADD_PROFIL_URL = "/admin/profile/ajout";
    private static final String MODIF_PROFIL_URL = "/admin/profile/modification?id=";

    public static final String FORM_PROFIL_HTML = "pages/admin/profile/profilForm";

    public SSProfils() {
        super();
    }

    protected ProfilUIService getProfilUIService() {
        return SSUIServiceLocator.getProfilUIService();
    }

    @GET
    @Path("liste")
    public ThTemplate getProfils(@QueryParam("ordreProfil") String ordreProfil) {
        verifyAction(SSActionEnum.ADMIN_USER_PROFILS, DATA_URL);
        template.setName("pages/admin/profile/liste");

        //Permet de rajouter les droits dans le contexte
        ProfilUIService profilUIService = getProfilUIService();
        profilUIService.computeListProfilActions(context);

        context.setNavigationContextTitle(new Breadcrumb(NAVIGATION_TITLE, DATA_URL, PROFILS_ORDER));
        context.putInContextData(SORT_ORDER, SortOrder.fromValue(ordreProfil));
        template.setContext(context);

        Map<String, Object> map = new HashMap<>();

        // récupérer la liste des profils par le service (triée en ASC par défaut)
        PageProfilDTO ppd = SSUIServiceLocator.getProfilUIService().getPageProfilDTO(context);

        map.put(SSTemplateConstants.PROFILS, ppd.getProfils());
        map.put(STTemplateConstants.LST_COLONNES, ppd.getLstColonnes());
        map.put(STTemplateConstants.DATA_URL, DATA_URL);
        map.put(STTemplateConstants.DATA_AJAX_URL, DATA_AJAX_URL);
        map.put(STTemplateConstants.CREATE_ACTION, context.getAction(SSActionEnum.ADMIN_PROFIL_CREATE));
        template.setData(map);
        return template;
    }

    @Path("/ficheProfil")
    public Object getFicheProfile() {
        verifyAction(SSActionEnum.ADMIN_USER_PROFILS, "/admin/profile/ficheProfil");

        template.setName("pages/admin/profile/ficheProfil");
        template.setData(new HashMap<>());
        return newObject("ProfilFicheAjax", context, template);
    }

    @GET
    @Path("/ajout")
    public ThTemplate newProfilForm() {
        verifyAction(SSActionEnum.ADMIN_USER_PROFILS, ADD_PROFIL_URL);
        context.setNavigationContextTitle(new Breadcrumb(NOUVEAU_PROFIL, ADD_PROFIL_URL, PROFILS_ORDER + 1));
        return profilForm(null);
    }

    @GET
    @Path("/modification")
    public ThTemplate updateProfilForm(@QueryParam("id") String id) {
        ProfilUIService profilUIService = getProfilUIService();
        context.putInContextData(PROFILE_ID, id);
        DocumentModel profilDoc = profilUIService.getProfilDoc(context);
        context.setCurrentDocument(profilDoc);
        profilUIService.computeProfilActions(context);

        verifyAction(SSActionEnum.ADMIN_PROFIL_EDIT, "/admin/profile/modification");
        context.setNavigationContextTitle(
            new Breadcrumb(MODIF_PROFIL + " " + id, MODIF_PROFIL_URL + id, PROFILS_ORDER + 2)
        );
        return profilForm(id);
    }

    private ThTemplate profilForm(String profilId) {
        ProfilUIService profilUIService = getProfilUIService();
        profilUIService.computeListProfilActions(context);

        FicheProfilDTO ficheProfilDTO = Optional
            .ofNullable(profilId)
            .flatMap(
                id -> {
                    context.putInContextData(PROFILE_ID, profilId);
                    return profilUIService.getOptionalProfilDoc(context);
                }
            )
            .map(
                doc -> {
                    context.setCurrentDocument(doc);
                    return profilUIService.getFicheProfilDTO(context);
                }
            )
            .orElseGet(FicheProfilDTO::new);

        ficheProfilDTO.setLstColonnes(
            Lists.newArrayList(new ColonneInfo("profil.fiche.header", false, true, false, true))
        );

        ficheProfilDTO.setId(profilId);
        List<String> fonctionsOfProfil = ficheProfilDTO
            .getFonctions()
            .stream()
            .map(SelectValueDTO::getId)
            .collect(Collectors.toList());

        boolean isCreation = StringUtils.isEmpty(profilId);
        if (isCreation) {
            template
                .getData()
                .put(STTemplateConstants.EDIT_ACTIONS, context.getActions(SSActionCategory.PROFIL_CREATION_ACTIONS));
        } else {
            template
                .getData()
                .put(
                    STTemplateConstants.EDIT_ACTIONS,
                    context.getActions(SSActionCategory.PROFIL_MODIFICATION_ACTIONS)
                );
        }

        template.getData().put("profilCreation", isCreation);

        template.getData().put(SSTemplateConstants.PROFIL, ficheProfilDTO);
        template.getData().put(SSTemplateConstants.FONCTIONS, profilUIService.getAllFunctions());
        template.getData().put(SSTemplateConstants.FONCTIONS_OF_PROFIL, fonctionsOfProfil);
        template.getData().put(STTemplateConstants.LST_COLONNES, ficheProfilDTO.getLstColonnes());
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.setContext(context);

        template.setName(FORM_PROFIL_HTML);
        return template;
    }

    @POST
    @Path("creer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProfil(@SwBeanParam FicheProfilForm ficheProfilForm) {
        ProfilUIService profilUIService = getProfilUIService();

        if (!profilUIService.getAllowCreateProfile(context)) {
            throw new STAuthorizationException("profil creation");
        }
        context.putInContextData(SSContextDataKey.PROFIL_FORM, ficheProfilForm);
        profilUIService.createProfile(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("modifier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfil(@SwBeanParam FicheProfilForm ficheProfilForm) {
        ProfilUIService profilUIService = getProfilUIService();

        context.putInContextData(PROFILE_ID, ficheProfilForm.getId());
        context.setCurrentDocument(profilUIService.getProfilDoc(context));
        if (!profilUIService.getAllowEditProfile(context)) {
            throw new STAuthorizationException("profil modification");
        }

        context.putInContextData(SSContextDataKey.PROFIL_FORM, ficheProfilForm);
        profilUIService.updateProfile(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return SSUIServiceLocator.getSSTemplateUIService().getLeftMenuTemplate(context);
    }
}
