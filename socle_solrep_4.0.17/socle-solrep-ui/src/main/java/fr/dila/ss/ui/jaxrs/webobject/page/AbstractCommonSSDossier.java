package fr.dila.ss.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STActionCategory.VIEW_ACTION_LIST;

import fr.dila.ss.ui.bean.SSConsultDossierDTO;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommonSSDossier extends SolonWebObject {

    protected void buildContextData(SpecificContext context, String id, String tab, String dossierLinkId) {
        //On indique en context l'onglet courant
        context.putInContextData(STContextDataKey.CURRENT_TAB, tab);
        context.setCurrentDocument(id);

        // Le dossierLinkId passé en paramètre pouvant être obsolète ou vide, on tente de récupérer le nouveau
        STDossierLink dossierLink = SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            dossierLinkId = dossierLink.getId();
        }
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
    }

    protected <T extends ThTemplate> void buildTemplateData(T template, String tab) {
        Map<String, Object> map = new HashMap<>();
        map.put(SSTemplateConstants.MON_DOSSIER, getConsultDossierDTO(context));
        map.put(SSTemplateConstants.ID_DOSSIER_LINK, context.getFromContextData(STContextDataKey.CURRENT_DOSSIER_LINK));
        map.put(STTemplateConstants.MY_TABS, OngletConteneur.actionsToTabs(context, VIEW_ACTION_LIST, tab));
        template.setData(map);
    }

    protected <T extends ThTemplate> void buildTemplateData(T template, String tab, String idMessage) {
        buildTemplateData(template, tab);
    }

    protected SSConsultDossierDTO getConsultDossierDTO(SpecificContext context) {
        return SSUIServiceLocator.getSSDossierUIService().getDossierConsult(context);
    }
}
