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
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.STGouvernementUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.model.STAdminTemplate;
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

@WebObject(type = "OrganigrammeGouvernement")
public class STOrganigrammeGouvernement extends SolonWebObject {
    public static final String NAVIGATION_TITLE_CREATION = "organigramme.gouvernement.create.title";
    public static final String NAVIGATION_TITLE_MODIFICATION = "organigramme.gouvernement.modif.title";

    public STOrganigrammeGouvernement() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new STAdminTemplate();
    }

    @GET
    @Path("/creation")
    public ThTemplate getGouvernementCreation() {
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.CREATE_GOUVERNEMENT, "/admin/organigramme/gouvernement/creation");

        ThTemplate template = getMyTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                "/admin/organigramme/gouvernement/creation",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editGouvernement");
        template.setContext(context);
        GouvernementForm createGvForm = new GouvernementForm();

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put("gouvernementForm", createGvForm);
        template.setData(map);

        return template;
    }

    @GET
    @Path("/modification")
    public ThTemplate getGouvernementModification(@QueryParam("idGvt") String idGvt) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode gouvernementNode = organigrammeService.getOrganigrammeNodeById(
            idGvt,
            OrganigrammeType.GOUVERNEMENT
        );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), gouvernementNode)
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.MODIFY_GOUVERNEMENT, "/admin/organigramme/gouvernement/modification");

        ThTemplate template = getMyTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                "/admin/organigramme/gouvernement/modification",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editGouvernement");
        template.setContext(context);

        context.putInContextData(STContextDataKey.ID, idGvt);
        GouvernementForm gouvernement = STUIServiceLocator.getSTGouvernementUIService().getGouvernementForm(context);

        Map<String, Object> map = new HashMap<>();
        map.put("gouvernementForm", gouvernement);
        template.setData(map);
        return template;
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("/sauvegarde")
    public Object saveOrUpdateGouvernement(@SwBeanParam GouvernementForm createGvForm) {
        context.putInContextData(STContextDataKey.GVT_FORM, createGvForm);

        STGouvernementUIService gouvernementUIService = STUIServiceLocator.getSTGouvernementUIService();
        String gouvernementId = createGvForm.getId();

        boolean isCreation = StringUtils.isBlank(gouvernementId);
        if (isCreation) {
            gouvernementUIService.createGouvernement(context);
        } else {
            gouvernementUIService.updateGouvernement(context);
        }

        // Récupération des messages d'erreur: en attente du back
        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(STOrganigramme.ORGANIGRAMME_URL);
        } else {
            return isCreation ? getGouvernementCreation() : getGouvernementModification(gouvernementId);
        }
    }
}
