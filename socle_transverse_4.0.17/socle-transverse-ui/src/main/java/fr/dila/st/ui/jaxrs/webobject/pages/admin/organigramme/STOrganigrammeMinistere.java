package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STOptinOptions;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.STMinistereUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.STMinistereUIServiceImpl;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeMinistere")
public class STOrganigrammeMinistere extends SolonWebObject implements SharedBetweenAdminAndUser {
    private static final String NAVIGATION_TITLE_CREATION = "Création de ministère";
    private static final String NAVIGATION_TITLE_MODIFICATION = "Modification de ministère";
    public static final String ENTITE_FORM = "entiteForm";

    public STOrganigrammeMinistere() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new STAdminTemplate();
    }

    @GET
    @Path("/creation")
    public ThTemplate getMinistereCreation(@QueryParam("idParent") String idParent) {
        // Récupérer le gouvernement à partir du param
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode gouvernement = organigrammeService.getOrganigrammeNodeById(
            idParent,
            OrganigrammeType.GOUVERNEMENT
        );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), gouvernement)
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.CREATE_ENTITE, "/admin/organigramme/ministere/creation");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                "/admin/organigramme/ministere/creation",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editMinistere");
        template.setContext(context);
        EntiteForm entiteForm;

        // Cas du réaffichage après erreur
        if (context.getFromContextData(STContextDataKey.ENTITE_FORM) != null) {
            entiteForm = context.getFromContextData(STContextDataKey.ENTITE_FORM);
        } else {
            entiteForm = new EntiteForm();
        }

        OrganigrammeElementDTO dtoGouvernement = new OrganigrammeElementDTO(context.getSession(), gouvernement);
        entiteForm.setGouvernement(dtoGouvernement);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(ENTITE_FORM, entiteForm);
        map.put("civiliteOptions", STOptinOptions.CIVILITE_OPTIONS);
        map.put(STMinistereUIServiceImpl.ID_PARENT, idParent);
        template.setData(map);

        return template;
    }

    @GET
    @Path("/modification")
    public ThTemplate getMinistereModification(@QueryParam("idMinistere") String idMinistere) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode ministere = organigrammeService.getOrganigrammeNodeById(
            idMinistere,
            OrganigrammeType.MINISTERE
        );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), ministere, ministere.getId())
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.MODIFY_ENTITE, "/admin/organigramme/ministere/modification");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                "/admin/organigramme/ministere/modification",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        // Changer le nom du fichier
        template.setName("pages/organigramme/editMinistere");
        template.setContext(context);
        EntiteForm entiteForm;

        // Cas du réaffichage après erreur
        if (context.getFromContextData(STContextDataKey.ENTITE_FORM) != null) {
            entiteForm = context.getFromContextData(STContextDataKey.ENTITE_FORM);
        } else {
            // Set contextData
            context.putInContextData(STContextDataKey.ID, idMinistere);
            // Récupérer le ministère à partir du param
            STMinistereUIService ministereUIService = STUIServiceLocator.getSTMinistereUIService();
            entiteForm = ministereUIService.getEntiteForm(context);
        }

        // Si gouvernement null - Récupérer le gouvernement à partir de l'id
        if (entiteForm.getGouvernement() == null) {
            OrganigrammeNode gouvernement = organigrammeService.getOrganigrammeNodeById(
                entiteForm.getIdGouvernement(),
                OrganigrammeType.GOUVERNEMENT
            );
            OrganigrammeElementDTO dtoGouvernement = new OrganigrammeElementDTO(context.getSession(), gouvernement);
            entiteForm.setGouvernement(dtoGouvernement);
        }

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(ENTITE_FORM, entiteForm);
        map.put("civiliteOptions", STOptinOptions.CIVILITE_OPTIONS);
        template.setData(map);

        return template;
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("/sauvegarde")
    public Object saveOrUpdateMinistere(@SwBeanParam EntiteForm entiteForm) {
        STMinistereUIService ministereUIService = STUIServiceLocator.getSTMinistereUIService();

        boolean isCreation = StringUtils.isBlank(entiteForm.getIdentifiant());

        context.putInContextData(STContextDataKey.ENTITE_FORM, entiteForm);

        if (isCreation) {
            ministereUIService.createEntite(context);
        } else {
            ministereUIService.updateEntite(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(STOrganigramme.ORGANIGRAMME_URL);
        } else {
            return isCreation
                ? getMinistereCreation(entiteForm.getIdGouvernement())
                : getMinistereModification(entiteForm.getIdentifiant());
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
