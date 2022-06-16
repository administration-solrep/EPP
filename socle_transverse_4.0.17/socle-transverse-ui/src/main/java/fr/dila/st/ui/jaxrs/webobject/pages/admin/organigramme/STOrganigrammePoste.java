package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STOptinOptions;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammePoste")
public class STOrganigrammePoste extends SolonWebObject implements SharedBetweenAdminAndUser {
    public static final String CUR_MIN = "curMin";
    private static final String NAVIGATION_TITLE_CREATION = "Création de poste";
    private static final String NAVIGATION_TITLE_MODIFICATION = "Modification de poste";
    public static final String POSTE_FORM = "posteForm";

    public STOrganigrammePoste() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new STAdminTemplate();
    }

    @GET
    @Path("creation")
    public ThTemplate getPosteCreation(
        @QueryParam("idParent") String idParent,
        @QueryParam(CUR_MIN) String ministereId
    ) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        String[] ids = idParent.split(";");
        OrganigrammeNode poste = organigrammeService.getOrganigrammeNodeById(
            ids[0],
            OrganigrammeType.UNITE_STRUCTURELLE
        );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), poste, ministereId)
        );

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.CREATE_POSTE, "/admin/organigramme/poste/creation");
        ThTemplate template = getMySharedTemplate();

        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                "/admin/organigramme/poste/creation",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editPoste");
        template.setContext(context);
        PosteForm posteForm;

        // Cas du réaffichage après erreur
        posteForm =
            ObjectHelper.requireNonNullElseGet(context.getFromContextData(STContextDataKey.POSTE_FORM), PosteForm::new);

        // Récupérer le ministère (ou l'unité structurelle) à partir du param
        if (StringUtils.isNotBlank(idParent)) {
            OrganigrammeTreeUIService treeService = STUIServiceLocator.getOrganigrammeTreeService();

            HashMap<String, String> mapParent = new HashMap<>();
            for (String id : idParent.split(";")) {
                OrganigrammeNode parentNode = treeService.findNodeHavingIdAndChildType(id, OrganigrammeType.POSTE);

                if (parentNode != null) {
                    mapParent.put(parentNode.getId(), parentNode.getLabel());
                }
            }
            posteForm.setMapUnitesStructurellesRattachement(mapParent);
        }

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(POSTE_FORM, posteForm);
        map.put(CUR_MIN, ministereId);
        map.put("civiliteOptions", STOptinOptions.CIVILITE_OPTIONS);
        template.setData(map);

        return template;
    }

    @GET
    @Path("modification")
    public ThTemplate getPosteModification(@QueryParam("id") String id, @QueryParam(CUR_MIN) String ministereId) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        PosteNode poste = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.POSTE);
        OrganigrammeNode parent = poste.getFirstUSParent();
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                poste,
                ministereId,
                new OrganigrammeElementDTO(context.getSession(), parent)
            )
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.MODIFY_POSTE, "/admin/organigramme/poste/modification");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                "/admin/organigramme/poste/modification",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        // Changer le nom du fichier
        template.setName("pages/organigramme/editPoste");
        template.setContext(context);
        PosteForm posteForm;

        // Cas du réaffichage après erreur
        if (context.getFromContextData(STContextDataKey.POSTE_FORM) != null) {
            posteForm = context.getFromContextData(STContextDataKey.POSTE_FORM);

            OrganigrammeTreeUIService treeService = STUIServiceLocator.getOrganigrammeTreeService();
            // récuếrer les nodes US et Ministere
            for (String idParent : posteForm.getUnitesStructurellesRattachement()) {
                OrganigrammeNode parentNode = treeService.findNodeHavingIdAndChildType(
                    idParent,
                    OrganigrammeType.POSTE
                );

                if (parentNode != null) {
                    posteForm.getMapUnitesStructurellesRattachement().put(parentNode.getId(), parentNode.getLabel());
                }
            }
        } else {
            // Set contextData
            context.putInContextData(STContextDataKey.ID, id);
            STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
            posteForm = posteUIService.getPosteForm(context);
        }

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(POSTE_FORM, posteForm);
        map.put(CUR_MIN, ministereId);
        map.put("civiliteOptions", STOptinOptions.CIVILITE_OPTIONS);
        template.setData(map);

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verifier")
    public Response checkUniqueLabel(@SwBeanParam PosteForm posteForm) {
        STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
        context.putInContextData(STContextDataKey.POSTE_FORM, posteForm);
        boolean isUniqueLabel = posteUIService.checkUniqueLabelPoste(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), isUniqueLabel).build();
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("sauvegarde")
    public Object saveOrUpdatePoste(@SwBeanParam PosteForm posteForm, @FormParam(CUR_MIN) String minParent) {
        STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
        String identifiant = posteForm.getId();

        boolean isCreation = StringUtils.isBlank(identifiant);

        context.putInContextData(STContextDataKey.POSTE_FORM, posteForm);

        if (isCreation) {
            // Création
            posteUIService.createPoste(context);
        } else {
            // Modification
            posteUIService.updatePoste(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(STOrganigramme.ORGANIGRAMME_URL);
        } else {
            // Concaténer les ids parent en une chaine de carractère pour le passer en paramètre de création
            String idParents = String.join(";", posteForm.getUnitesStructurellesRattachement());

            return isCreation ? getPosteCreation(idParents, minParent) : getPosteModification(identifiant, minParent);
        }
    }

    @Override
    public ThTemplate getMyAdminTemplate() {
        return new STAdminTemplate();
    }

    @Override
    public ThTemplate getMyUserTemplate() {
        return new STUtilisateurTemplate();
    }
}
