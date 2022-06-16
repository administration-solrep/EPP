package fr.dila.ss.core.service;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.cm.service.CaseDistributionService;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.pdf.SSPdfService;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSArchiveService;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.api.service.SSExcelService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.api.service.SSMinisteresService;
import fr.dila.ss.api.service.SSProfilUtilisateurService;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.api.service.SSUserService;
import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.nuxeo.ecm.platform.comment.api.CommentManager;

/**
 * ServiceLocator du socle SOLREP : permet de rechercher les services du socle SOLREP.
 *
 * @author jtremeaux
 */
public final class SSServiceLocator {

    /**
     * utility class
     */
    private SSServiceLocator() {
        // do nothing
    }

    public static CaseDistributionService getCaseDistributionService() {
        return ServiceUtil.getRequiredService(CaseDistributionService.class);
    }

    public static DossierDistributionService getDossierDistributionService() {
        return ServiceUtil.getRequiredService(DossierDistributionService.class);
    }

    public static DocumentRoutingService getDocumentRoutingService() {
        return ServiceUtil.getRequiredService(DocumentRoutingService.class);
    }

    public static FeuilleRouteModelService getFeuilleRouteModelService() {
        return ServiceUtil.getRequiredService(FeuilleRouteModelService.class);
    }

    public static MailboxPosteService getMailboxPosteService() {
        return ServiceUtil.getRequiredService(MailboxPosteService.class);
    }

    public static SSFeuilleRouteService getSSFeuilleRouteService() {
        return ServiceUtil.getRequiredService(SSFeuilleRouteService.class);
    }

    public static SSBirtService getSSBirtService() {
        return ServiceUtil.getRequiredService(SSBirtService.class);
    }

    public static BirtGenerationService getBirtGenerationService() {
        return ServiceUtil.getRequiredService(BirtGenerationService.class);
    }

    public static CommentManager getCommentManager() {
        return ServiceUtil.getRequiredService(CommentManager.class);
    }

    public static SSJournalService getSSJournalService() {
        return ServiceUtil.getRequiredService(SSJournalService.class);
    }

    public static SSTreeService getSSTreeService() {
        return ServiceUtil.getRequiredService(SSTreeService.class);
    }

    public static ActualiteService getActualiteService() {
        return ServiceUtil.getRequiredService(ActualiteService.class);
    }

    public static SSProfilUtilisateurService getSsProfilUtilisateurService() {
        return ServiceUtil.getRequiredService(SSProfilUtilisateurService.class);
    }

    public static SSArchiveService getSSArchiveService() {
        return ServiceUtil.getRequiredService(SSArchiveService.class);
    }

    public static SSFondDeDossierService getSSFondDeDossierService() {
        return ServiceUtil.getRequiredService(SSFondDeDossierService.class);
    }

    public static SSExcelService getSSExcelService() {
        return ServiceUtil.getRequiredService(SSExcelService.class);
    }

    public static RoutingTaskTypeService getRoutingTaskTypeService() {
        return getRequiredService(RoutingTaskTypeService.class);
    }

    public static SSPdfService getSSPdfService() {
        return ServiceUtil.getRequiredService(SSPdfService.class);
    }

    public static SSMinisteresService getSSMinisteresService() {
        return getRequiredService(SSMinisteresService.class);
    }

    public static SSUserService getSSUserService() {
        return getRequiredService(SSUserService.class);
    }
}
