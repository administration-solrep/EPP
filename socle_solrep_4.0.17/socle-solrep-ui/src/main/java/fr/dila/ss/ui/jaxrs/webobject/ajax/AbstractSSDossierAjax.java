package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.webobject.page.AbstractCommonSSDossier;
import fr.dila.ss.ui.jaxrs.webobject.page.AbstractSSDossier;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.validators.annot.SwId;
import fr.dila.st.ui.validators.annot.SwNotEmpty;
import java.util.Objects;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractSSDossierAjax extends AbstractCommonSSDossier {
    public static final String DOSSIER_AJAX_WEBOBJECT = "AjaxDossier";

    protected static final String RESULT_OK = "result : OK";

    protected AbstractSSDossierAjax() {}

    @Path("{id}/{tab}")
    public Object getDossier(
        @PathParam("id") String id,
        @PathParam("tab") String tab,
        @QueryParam("dossierLinkId") @SwId String dossierLinkId,
        @QueryParam("isMgpp") boolean isMgpp
    ) {
        Objects.requireNonNull(id);
        if (StringUtils.length(tab) < 2) {
            throw new IllegalArgumentException("tab est mal renseigné");
        }

        buildContextData(context, id, tab, dossierLinkId);
        if (isMgpp) {
            return newObject(AbstractSSDossier.DOSSIER_WEBOBJECT + StringUtils.capitalize(tab), context);
        }
        AjaxLayoutThTemplate template = new AjaxLayoutThTemplate("pages/dossier/consult-dossier-content", context);
        buildTemplateData(template, tab);
        SSUIServiceLocator.getSSDossierUIService().loadDossierActions(context, template);

        tab = tab.substring(0, 1).toUpperCase() + tab.substring(1);
        return newObject(AbstractSSDossier.DOSSIER_WEBOBJECT + tab, context, template);
    }

    @Path("save/{tab}")
    public Object saveDossier(
        @FormParam("dossierId") @SwNotEmpty @SwId String id,
        @FormParam("dossierLinkId") @SwId String dossierLinkId,
        @PathParam("tab") @SwNotEmpty String tab
    ) {
        context.setCurrentDocument(id);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        return newObject("AppliDossier" + StringUtils.capitalize(tab), context);
    }

    @Path("unread")
    @POST
    public String unreadDossier(@FormParam("dossierId") String id, @FormParam("dossierLinkId") String dossierLinkId) {
        SpecificContext context = new SpecificContext();

        context.setCurrentDocument(id);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        context.putInContextData(SSContextDataKey.DOSSIER_IS_READ, false);

        SSUIServiceLocator.getSSDossierDistributionUIService().changeReadStateDossierLink(context);
        STActionsServiceLocator.getDossierLockActionService().unlockCurrentDossier(context);

        return RESULT_OK;
    }

    @Path("{id}/substitution")
    public Object getSubstitution(@PathParam("id") String id) {
        context.setCurrentDocument(id);
        return newObject("SubstitutionFDRAjax", context);
    }
}
