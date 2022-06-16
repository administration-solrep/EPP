package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

@WebObject(type = "OrganigrammePosteWs")
public class STOrganigrammePosteWs extends SolonWebObject implements SharedBetweenAdminAndUser {
    private static final String NAVIGATION_TITLE_CREATION = "Création de poste web service";
    private static final String NAVIGATION_TITLE_MODIFICATION = "Modification de poste web service";

    public STOrganigrammePosteWs() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new STAdminTemplate();
    }

    @GET
    @Path("creation")
    public ThTemplate getPosteWsCreation(@QueryParam("idParent") String idParent) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode parent = organigrammeService.getOrganigrammeNodeById(idParent, OrganigrammeType.MINISTERE);
        if (parent == null) {
            parent = organigrammeService.getOrganigrammeNodeById(idParent, OrganigrammeType.INSTITUTION);
        }

        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), parent, idParent)
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.CREATE_POSTE_WS, "/admin/organigramme/posteWs/creation");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                "/admin/organigramme/posteWs/creation",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        context.putInContextData("idParent", idParent);
        template.setName("pages/organigramme/editPosteWs");
        template.setContext(context);

        PosteWsForm posteWsForm = new PosteWsForm();
        posteWsForm.setMapMinisteres(new HashMap<>());
        posteWsForm.setMapInstitution(new HashMap<>());

        if (StringUtils.isNotBlank(idParent)) {
            OrganigrammeTreeUIService treeService = STUIServiceLocator.getOrganigrammeTreeService();

            OrganigrammeNode parentNode = treeService.findNodeHavingIdAndChildType(idParent, OrganigrammeType.POSTE);

            if (parentNode != null) {
                if (parentNode.getType() == OrganigrammeType.MINISTERE) {
                    posteWsForm.setMapMinisteres(
                        new HashMap<>(Collections.singletonMap(parentNode.getId(), parentNode.getLabel()))
                    );
                } else if (parentNode.getType() == OrganigrammeType.INSTITUTION) {
                    posteWsForm.setMapInstitution(
                        new HashMap<>(Collections.singletonMap(parentNode.getId(), parentNode.getLabel()))
                    );
                }
            }
        }

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put("posteWsForm", posteWsForm);
        template.setData(map);

        return template;
    }

    @GET
    @Path("modification")
    public ThTemplate getPosteWsModification(@QueryParam("id") String id) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        PosteNode poste = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.POSTE);

        OrganigrammeNode parent = Optional
            .ofNullable((OrganigrammeNode) poste.getFirstEntiteParent())
            .orElseGet(
                () ->
                    organigrammeService.getOrganigrammeNodeById(
                        poste.getParentInstitIds().get(0),
                        OrganigrammeType.INSTITUTION
                    )
            );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                poste,
                parent.getId(),
                new OrganigrammeElementDTO(context.getSession(), parent)
            )
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.MODIFY_POSTE_WS, "/admin/organigramme/posteWs/modification");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                "/admin/organigramme/posteWs/modification",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editPosteWs");
        template.setContext(context);

        context.putInContextData(STContextDataKey.ID, id);
        STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
        PosteWsForm posteWsForm = posteUIService.getPosteWsForm(context);

        // Je renvoie mon formulaire en sortie
        template.getData().put("posteWsForm", posteWsForm);

        return template;
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("sauvegarde")
    public Object createOrUpdatePosteWs(@SwBeanParam PosteWsForm posteWsForm) {
        String identifiant = posteWsForm.getId();
        // Set contextData
        context.putInContextData(STContextDataKey.POSTE_WS_FORM, posteWsForm);

        STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
        if (StringUtils.isNotBlank(identifiant)) {
            posteUIService.updatePosteWs(context);
        } else {
            posteUIService.createPosteWs(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(STOrganigramme.ORGANIGRAMME_URL);
        } else {
            // sinon retour à l'écran en cours (création ou modification)
            return null;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verifier")
    public Response checkUniqueLabel(@SwBeanParam PosteWsForm posteWsForm) {
        STPosteUIService posteUIService = STUIServiceLocator.getSTPosteUIService();
        context.putInContextData(STContextDataKey.POSTE_WS_FORM, posteWsForm);
        boolean isUniqueLabel = posteUIService.checkUniqueLabelPosteWs(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), isUniqueLabel).build();
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
