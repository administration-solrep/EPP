package fr.dila.epp.ui.services;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.st.ui.services.NotificationUIService;

public final class SolonEppUIServiceLocator {

    /**
     * Utility class
     */
    private SolonEppUIServiceLocator() {
        // do nothing
    }

    public static EvenementActionsUIService getEvenementActionsUIService() {
        return getRequiredService(EvenementActionsUIService.class);
    }

    public static EvenementUIService getEvenementUIService() {
        return getRequiredService(EvenementUIService.class);
    }

    public static HistoriqueDossierUIService getHistoriqueDossierUIService() {
        return getRequiredService(HistoriqueDossierUIService.class);
    }

    public static MessageListUIService getMessageListUIService() {
        return getRequiredService(MessageListUIService.class);
    }

    public static MetadonneesUIService getMetadonneesUIService() {
        return getRequiredService(MetadonneesUIService.class);
    }

    public static RechercheUIService getRechercheUIService() {
        return getRequiredService(RechercheUIService.class);
    }

    public static SelectValueUIService getSelectValueUIService() {
        return getRequiredService(SelectValueUIService.class);
    }

    public static VersionUIService getVersionUIService() {
        return getRequiredService(VersionUIService.class);
    }

    public static EPPOrganigrammeManagerService getOrganigrammeService() {
        return getRequiredService(EPPOrganigrammeManagerService.class);
    }

    public static EppProfilUIService getProfilService() {
        return getRequiredService(EppProfilUIService.class);
    }

    public static NotificationUIService getNotificationUIService() {
        return getRequiredService(NotificationUIService.class);
    }
}
