package fr.dila.ss.ui.services.impl;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.SSFdrUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.ui.bean.actions.DossierLockActionDTO;
import fr.dila.st.ui.bean.actions.STLockActionDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSFdrUIServiceImpl implements SSFdrUIService {
    public static final String CAN_USER_SUBSTITUER_FDR = "canUserSubstituerFdr";

    @Override
    public FdrDTO getFeuilleDeRoute(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        STDossier dossier = dossierDoc.getAdapter(STDossier.class);
        context.putInContextData(STContextDataKey.ID, dossier.getLastDocumentRoute());
        setActionsInContext(context, dossierDoc);

        FdrDTO dto = new FdrDTO();
        dto.setId(dossier.getLastDocumentRoute());
        dto.setTable(SSUIServiceLocator.getSSFeuilleRouteUIService().getFeuilleRouteDTO(context));
        dto.buildColonnesFdr();

        dto.setTabActions(context.getActions(SSActionCategory.FDR_STEP_ACTIONS));
        dto.setSecondTabActions(context.getActions(SSActionCategory.FDR_ACTIONS));
        // Set la feuille de route comme currentDoc
        String fdrId = context.getFromContextData(STContextDataKey.ID);
        context.setCurrentDocument(fdrId);
        context.putInContextData(
            CAN_USER_SUBSTITUER_FDR,
            SSActionsServiceLocator.getDocumentRoutingActionService().canUserSubstituerFeuilleRoute(context)
        );
        dto.setSubstitutionAction(context.getAction(SSActionEnum.DOSSIER_SUBSTITUER_FDR));
        return dto;
    }

    private void setActionsInContext(SpecificContext context, DocumentModel currentDoc) {
        DossierLockActionDTO dossierLockAction;
        if (context.containsKeyInContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS)) {
            dossierLockAction = context.getFromContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS);
        } else {
            dossierLockAction = new DossierLockActionDTO();
            context.putInContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS, dossierLockAction);
        }

        if (!dossierLockAction.getCanLockCurrentDossier()) {
            dossierLockAction.setCanLockCurrentDossier(
                STActionsServiceLocator.getDossierLockActionService().getCanLockCurrentDossier(context)
            );
        }

        STLockActionDTO stLockAction;
        if (context.containsKeyInContextData(STContextDataKey.LOCK_ACTIONS)) {
            stLockAction = context.getFromContextData(STContextDataKey.LOCK_ACTIONS);
        } else {
            stLockAction = new STLockActionDTO();
            context.putInContextData(STContextDataKey.LOCK_ACTIONS, stLockAction);
        }

        if (!stLockAction.getCurrentDocIsLockActionnableByCurrentUser()) {
            stLockAction.setCurrentDocIsLockActionnableByCurrentUser(
                STActionsServiceLocator
                    .getSTLockActionService()
                    .currentDocIsLockActionnableByCurrentUser(
                        context.getSession(),
                        currentDoc,
                        context.getSession().getPrincipal()
                    )
            );
        }
    }
}
