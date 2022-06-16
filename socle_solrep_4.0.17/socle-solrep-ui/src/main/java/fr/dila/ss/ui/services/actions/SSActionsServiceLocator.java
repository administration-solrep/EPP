package fr.dila.ss.ui.services.actions;

import fr.dila.ss.ui.services.organigramme.SSMigrationManagerUIService;
import fr.dila.ss.ui.services.organigramme.SSOrganigrammeManagerUIService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

/**
 * Locator de services action (anciens action beans).
 *
 * @author tlombard
 */
public final class SSActionsServiceLocator {

    /**
     * Utility class
     */
    private SSActionsServiceLocator() {
        // do nothing
    }

    public static SSDocumentRoutingActionService getDocumentRoutingActionService() {
        return ServiceUtil.getRequiredService(SSDocumentRoutingActionService.class);
    }

    public static RelatedRouteActionService getRelatedRouteActionService() {
        return ServiceUtil.getRequiredService(RelatedRouteActionService.class);
    }

    public static NavigationActionService getNavigationActionService() {
        return ServiceUtil.getRequiredService(NavigationActionService.class);
    }

    public static RouteStepNoteActionService getRouteStepNoteActionService() {
        return ServiceUtil.getRequiredService(RouteStepNoteActionService.class);
    }

    public static ModeleFeuilleRouteActionService getModeleFeuilleRouteActionService() {
        return ServiceUtil.getRequiredService(ModeleFeuilleRouteActionService.class);
    }

    public static SSOrganigrammeManagerUIService getSSOrganigrammeManagerActionService() {
        return ServiceUtil.getRequiredService(SSOrganigrammeManagerUIService.class);
    }

    public static SSMigrationManagerUIService getSSMigrationManagerActionService() {
        return ServiceUtil.getRequiredService(SSMigrationManagerUIService.class);
    }

    public static SSRechercheModeleFeuilleRouteActionService getRechercheModeleFeuilleRouteActionService() {
        return ServiceUtil.getRequiredService(SSRechercheModeleFeuilleRouteActionService.class);
    }

    public static SSAlertActionService getAlertActionService() {
        return ServiceUtil.getRequiredService(SSAlertActionService.class);
    }

    public static SSCorbeilleActionService getSSCorbeilleActionService() {
        return ServiceUtil.getRequiredService(SSCorbeilleActionService.class);
    }

    public static FeuilleRouteActionService getFeuilleRouteActionService() {
        return ServiceUtil.getRequiredService(FeuilleRouteActionService.class);
    }
}
