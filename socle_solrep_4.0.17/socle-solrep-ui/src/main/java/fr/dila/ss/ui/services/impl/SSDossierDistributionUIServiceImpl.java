package fr.dila.ss.ui.services.impl;

import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSDossierDistributionUIService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;

public class SSDossierDistributionUIServiceImpl implements SSDossierDistributionUIService {

    public void changeReadStateDossierLink(SpecificContext context) {
        STDossierLink currentDossierLink = SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        CoreSession session = context.getSession();
        if (currentDossierLink != null) {
            // remet le tag "non lu"
            currentDossierLink.setReadState(context.getFromContextData(SSContextDataKey.DOSSIER_IS_READ));
            session.saveDocument(currentDossierLink.getDocument());
        }
    }
}
