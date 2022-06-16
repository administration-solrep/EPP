package fr.dila.st.ui.services.actions;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class STActionsServiceLocator {

    /**
     * Utility class
     */
    private STActionsServiceLocator() {
        // do nothing
    }

    public static DossierLockActionService getDossierLockActionService() {
        return ServiceUtil.getRequiredService(DossierLockActionService.class);
    }

    public static STLockActionService getSTLockActionService() {
        return ServiceUtil.getRequiredService(STLockActionService.class);
    }

    public static STCorbeilleActionService getSTCorbeilleActionService() {
        return ServiceUtil.getRequiredService(STCorbeilleActionService.class);
    }

    public static ProfileSuggestionActionService getProfileSuggestionActionService() {
        return ServiceUtil.getRequiredService(ProfileSuggestionActionService.class);
    }
}
