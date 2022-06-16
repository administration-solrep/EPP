package fr.dila.ss.ui.services;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.bean.SSConsultDossierDTO;
import fr.dila.ss.ui.services.actions.suggestion.feuilleroute.FeuilleRouteSuggestionProviderService;
import fr.dila.ss.ui.services.comment.SSCommentManagerUIService;
import fr.dila.ss.ui.services.organigramme.SSMigrationManagerUIService;
import fr.dila.ss.ui.services.organigramme.SSOrganigrammeManagerUIService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public final class SSUIServiceLocator {

    /**
     * Utility class
     */
    private SSUIServiceLocator() {
        // do nothing
    }

    public static SSOrganigrammeManagerService getSSOrganigrammeManagerService() {
        return getRequiredService(SSOrganigrammeManagerService.class);
    }

    public static SSFeuilleRouteUIService getSSFeuilleRouteUIService() {
        return getRequiredService(SSFeuilleRouteUIService.class);
    }

    public static SSMigrationManagerUIService getSSMigrationManagerActionService() {
        return getRequiredService(SSMigrationManagerUIService.class);
    }

    public static ActualiteUIService getActualiteUIService() {
        return getRequiredService(ActualiteUIService.class);
    }

    public static ProfilUIService getProfilUIService() {
        return getRequiredService(ProfilUIService.class);
    }

    public static SSCommentManagerUIService getSSCommentManagerUIService() {
        return getRequiredService(SSCommentManagerUIService.class);
    }

    public static SSParametreUIService getParametreUIService() {
        return getRequiredService(SSParametreUIService.class);
    }

    public static FeuilleRouteSuggestionProviderService getFeuilleRouteSuggestionProviderService() {
        return getRequiredService(FeuilleRouteSuggestionProviderService.class);
    }

    @SuppressWarnings("unchecked")
    public static SSJournalUIService<JournalDossierResultList> getJournalUIService() {
        return getRequiredService(SSJournalUIService.class);
    }

    public static SSJournalAdminUIService getJournalAdminUIService() {
        return getRequiredService(SSJournalAdminUIService.class);
    }

    public static SSSupervisionUIService getSupervisionUIService() {
        return getRequiredService(SSSupervisionUIService.class);
    }

    public static SSArchiveUIService getSSArchiveUIService() {
        return getRequiredService(SSArchiveUIService.class);
    }

    public static SSModeleFdrFicheUIService getSSModeleFdrFicheUIService() {
        return getRequiredService(SSModeleFdrFicheUIService.class);
    }

    public static SSTemplateUIService getSSTemplateUIService() {
        return getRequiredService(SSTemplateUIService.class);
    }

    public static SSFdrUIService getSSFdrUIService() {
        return getRequiredService(SSFdrUIService.class);
    }

    public static SSSelectValueUIService getSSSelectValueUIService() {
        return getRequiredService(SSSelectValueUIService.class);
    }

    public static SSMailboxListComponentService getSSMailboxListComponentService() {
        return getRequiredService(SSMailboxListComponentService.class);
    }

    public static SSDossierDistributionUIService getSSDossierDistributionUIService() {
        return getRequiredService(SSDossierDistributionUIService.class);
    }

    public static SSBirtUIService getSSBirtUIService() {
        return getRequiredService(SSBirtUIService.class);
    }

    public static SSStatistiquesUIService getSSStatistiquesUIService() {
        return getRequiredService(SSStatistiquesUIService.class);
    }

    public static SSOrganigrammeManagerUIService getSSOrganigrammeManagerUIService() {
        return getRequiredService(SSOrganigrammeManagerUIService.class);
    }

    @SuppressWarnings("unchecked")
    public static SSDossierUIService<SSConsultDossierDTO> getSSDossierUIService() {
        return ServiceUtil.getRequiredService(SSDossierUIService.class);
    }
}
