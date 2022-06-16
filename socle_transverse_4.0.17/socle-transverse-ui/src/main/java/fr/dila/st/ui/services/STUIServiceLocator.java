package fr.dila.st.ui.services;

import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;
import fr.dila.st.ui.services.batch.SuiviBatchUIService;
import fr.dila.st.ui.services.mail.MailUIService;
import fr.dila.st.ui.th.ThEngineService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public final class STUIServiceLocator {

    /**
     * Utility class
     */
    private STUIServiceLocator() {
        // do nothing
    }

    public static ConfigUIService getConfigUIService() {
        return ServiceUtil.getRequiredService(ConfigUIService.class);
    }

    public static PasswordService getPasswordService() {
        return ServiceUtil.getRequiredService(PasswordService.class);
    }

    public static OrganigrammeTreeUIService getOrganigrammeTreeService() {
        return ServiceUtil.getRequiredService(OrganigrammeTreeUIService.class);
    }

    public static STPosteUIService getSTPosteUIService() {
        return ServiceUtil.getRequiredService(STPosteUIService.class);
    }

    public static STUniteStructurelleUIService getSTUniteStructurelleUIService() {
        return ServiceUtil.getRequiredService(STUniteStructurelleUIService.class);
    }

    public static STMinistereUIService getSTMinistereUIService() {
        return ServiceUtil.getRequiredService(STMinistereUIService.class);
    }

    public static STUtilisateursUIService getSTUtilisateursUIService() {
        return ServiceUtil.getRequiredService(STUtilisateursUIService.class);
    }

    public static STGouvernementUIService getSTGouvernementUIService() {
        return ServiceUtil.getRequiredService(STGouvernementUIService.class);
    }

    public static ThEngineService getThEngineService() {
        return ServiceUtil.getRequiredService(ThEngineService.class);
    }

    public static SuiviBatchUIService getSuiviBatchUIService() {
        return ServiceUtil.getRequiredService(SuiviBatchUIService.class);
    }

    public static NomAuteurSuggestionProviderService getNomAuteurSuggestionProviderService() {
        return ServiceUtil.getRequiredService(NomAuteurSuggestionProviderService.class);
    }

    public static STRechercheUtilisateursUIService getRechercheUtilisateursUIService() {
        return ServiceUtil.getRequiredService(STRechercheUtilisateursUIService.class);
    }

    public static EtatApplicationUIService getEtatApplicationUIService() {
        return ServiceUtil.getRequiredService(EtatApplicationUIService.class);
    }

    public static MailUIService getMailUIService() {
        return ServiceUtil.getRequiredService(MailUIService.class);
    }

    public static CSRFService getCSRFService() {
        return ServiceUtil.getRequiredService(CSRFService.class);
    }

    public static STOrganigrammeManagerService getSTOrganigrammeManagerService() {
        return ServiceUtil.getRequiredService(STOrganigrammeManagerService.class);
    }

    public static STUserManagerUIService getSTUserManagerUIService() {
        return ServiceUtil.getRequiredService(STUserManagerUIService.class);
    }
}
