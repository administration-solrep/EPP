package fr.dila.epp.ui.services.actions;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

public final class SolonEppActionsServiceLocator {

    /**
     * Utility class
     */
    private SolonEppActionsServiceLocator() {
        // do nothing
    }

    public static CorbeilleActionService getCorbeilleActionService() {
        return getRequiredService(CorbeilleActionService.class);
    }

    public static EvenementTypeActionService getEvenementTypeActionService() {
        return getRequiredService(EvenementTypeActionService.class);
    }

    public static MetadonneesActionService getMetadonneesActionService() {
        return getRequiredService(MetadonneesActionService.class);
    }
}
