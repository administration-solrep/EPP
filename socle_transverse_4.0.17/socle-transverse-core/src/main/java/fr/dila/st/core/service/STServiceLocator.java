package fr.dila.st.core.service;

import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.st.api.service.CleanupService;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.api.service.STCorbeilleService;
import fr.dila.st.api.service.STDirectoryService;
import fr.dila.st.api.service.STExportService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.service.STRequeteurWidgetGeneratorService;
import fr.dila.st.api.service.STTokenService;
import fr.dila.st.api.service.STUserSearchService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.STVersionApplicationService;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.api.trash.TrashService;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.EventServiceAdmin;
import org.nuxeo.ecm.core.io.download.DownloadService;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.ui.web.auth.service.BruteforceSecurityService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.ecm.platform.versioning.api.VersioningManager;

/**
 * ServiceLocator du socle transverse : permet de rechercher les services du socle technique (Nuxeo) ou du socle
 * transverse.
 *
 * @author jtremeaux
 */
public class STServiceLocator {

    /**
     * utility class
     */
    protected STServiceLocator() {
        // do nothing
    }

    /**
     * Retourne le service PathSegmentService.
     *
     * @return service PathSegmentService
     */
    public static PathSegmentService getPathSegmentService() {
        return ServiceUtil.getRequiredService(PathSegmentService.class);
    }

    /**
     * Retourne le service ProfileService.
     *
     * @return service ProfileService
     */
    public static ProfileService getProfileService() {
        return ServiceUtil.getRequiredService(ProfileService.class);
    }

    /**
     * Retourne le service UserManager.
     *
     * @return service UserManager
     */
    public static UserManager getUserManager() {
        return ServiceUtil.getRequiredService(UserManager.class);
    }

    /**
     * Retourne le service UserWorkspace.
     *
     * @return service UserWorkspace
     */
    public static UserWorkspaceService getUserWorkspaceService() {
        return ServiceUtil.getRequiredService(UserWorkspaceService.class);
    }

    /**
     * Retourne le service RepositoryManager.
     *
     * @return service RepositoryManager
     */
    public static RepositoryManager getRepositoryManager() {
        return ServiceUtil.getRequiredService(RepositoryManager.class);
    }

    /**
     * Retourne le service VersioningManager.
     *
     * @return service VersioningManager
     */
    public static VersioningManager getVersioningManager() {
        return ServiceUtil.getRequiredService(VersioningManager.class);
    }

    /**
     * Retourne le service CaseManagementDocumentTypeService.
     *
     * @return service CaseManagementDocumentTypeService
     */
    public static CaseManagementDocumentTypeService getCaseManagementDocumentTypeService() {
        return ServiceUtil.getRequiredService(CaseManagementDocumentTypeService.class);
    }

    /**
     * Retourne le service DirectoryService.
     *
     * @return service DirectoryService
     */
    public static DirectoryService getDirectoryService() {
        return ServiceUtil.getRequiredService(DirectoryService.class);
    }

    /**
     * Retourne le service EventServiceAdmin.
     *
     * @return service EventServiceAdmin
     */
    public static EventServiceAdmin getEventServiceAdmin() {
        return ServiceUtil.getRequiredService(EventServiceAdmin.class);
    }

    /**
     * Retourne le service ConfigService.
     *
     * @return service ConfigService
     */
    public static ConfigService getConfigService() {
        return ServiceUtil.getRequiredService(ConfigService.class);
    }

    /**
     * Retourne le service EventProducer.
     *
     * @return service EventProducer
     */
    public static EventProducer getEventProducer() {
        return ServiceUtil.getRequiredService(EventProducer.class);
    }

    /**
     * Retourne le service Organigramme.
     *
     * @return service Organigramme
     */
    public static OrganigrammeService getOrganigrammeService() {
        return ServiceUtil.getRequiredService(OrganigrammeService.class);
    }

    /**
     * Retourne le service STLockService.
     *
     * @return service STLockService
     */
    public static STLockService getSTLockService() {
        return ServiceUtil.getRequiredService(STLockService.class);
    }

    /**
     * Retourne le service Journal.
     *
     * @return service Journal
     */
    public static JournalService getJournalService() {
        return ServiceUtil.getRequiredService(JournalService.class);
    }

    /**
     * Retourne le service Mailbox.
     *
     * @return service Mailbox
     */
    public static MailboxService getMailboxService() {
        return ServiceUtil.getRequiredService(MailboxService.class);
    }

    /**
     * Retourne le service Security.
     *
     * @return service Security
     */
    public static SecurityService getSecurityService() {
        return ServiceUtil.getRequiredService(SecurityService.class);
    }

    /**
     * Retourne le service TrashService.
     *
     * @return service Mail
     */
    public static TrashService getTrashService() {
        return ServiceUtil.getRequiredService(TrashService.class);
    }

    /**
     * Retourne le service Mail du socle.
     *
     * @return service Mail
     */
    public static STMailService getSTMailService() {
        return ServiceUtil.getRequiredService(STMailService.class);
    }

    /**
     *
     * Retourne le service de vocabulaire.
     *
     * @return service Vocabulaire
     */

    public static VocabularyService getVocabularyService() {
        return ServiceUtil.getRequiredService(VocabularyService.class);
    }

    public static STDirectoryService getSTDirectoryService() {
        return ServiceUtil.getRequiredService(STDirectoryService.class);
    }

    /**
     * Retourne le service STUserService.
     *
     * @return service STUserService
     */
    public static STUserService getSTUserService() {
        return ServiceUtil.getRequiredService(STUserService.class);
    }

    /**
     * Retourne le service STParametreService.
     *
     * @return service STParametreService
     */
    public static STParametreService getSTParametreService() {
        return ServiceUtil.getRequiredService(STParametreService.class);
    }

    /**
     * Retourn le service Alerte
     *
     * @return service Alert
     */
    public static STAlertService getAlertService() {
        return ServiceUtil.getRequiredService(STAlertService.class);
    }

    /**
     *
     * Retourne le service requeteur
     *
     * @return service RequeteurService
     */
    public static RequeteurService getRequeteurService() {
        return ServiceUtil.getRequiredService(RequeteurService.class);
    }

    /**
     * Retourne le service de jointure du projet,
     *
     * @return service JointureService
     */
    public static JointureService getJointureService() {
        return ServiceUtil.getRequiredService(JointureService.class);
    }

    /**
     * Retourne le service de suivi des batchs.
     *
     * @return service SuiviBatchService
     */
    public static SuiviBatchService getSuiviBatchService() {
        return ServiceUtil.getRequiredService(SuiviBatchService.class);
    }

    /**
     * Retourne le service de notification de suivi des batchs
     *
     * @return NotificationsSuiviBatchsService
     */
    public static NotificationsSuiviBatchsService getNotificationsSuiviBatchsService() {
        return ServiceUtil.getRequiredService(NotificationsSuiviBatchsService.class);
    }

    /**
     * Retourne le service EtatApplicationService
     *
     * @return EtatApplicationService
     */
    public static EtatApplicationService getEtatApplicationService() {
        return ServiceUtil.getRequiredService(EtatApplicationService.class);
    }

    /**
     * Retourne le service MailboxCreator
     *
     * @return MailboxCreator
     */
    public static MailboxCreator getMailboxCreator() {
        return ServiceUtil.getRequiredService(MailboxCreator.class);
    }

    /**
     * Retourne le service de génération des widgets du requêteur
     *
     * @return {@link STRequeteurWidgetGeneratorService}
     */
    public static STRequeteurWidgetGeneratorService getSTRequeteurWidgetGeneratorService() {
        return ServiceUtil.getRequiredService(STRequeteurWidgetGeneratorService.class);
    }

    /**
     * Retourne le service pour supprimer des documents deleted
     *
     * @return
     */
    public static CleanupService getCleanupService() {
        return ServiceUtil.getRequiredService(CleanupService.class);
    }

    /**
     *
     * Retourne le service de jeton
     *
     * @return service Jeton
     */
    public static JetonService getJetonService() {
        return ServiceUtil.getRequiredService(JetonService.class);
    }

    public static EventService getEventService() {
        return ServiceUtil.getRequiredService(EventService.class);
    }

    public static STExportService getSTExportService() {
        return ServiceUtil.getRequiredService(STExportService.class);
    }

    /**
     * Retourne le service de ldap gourvernement
     *
     * @return
     */
    public static STGouvernementService getSTGouvernementService() {
        return ServiceUtil.getRequiredService(STGouvernementService.class);
    }

    /**
     * Retourne le service de ldap ministères
     *
     * @return
     */
    public static STMinisteresService getSTMinisteresService() {
        return ServiceUtil.getRequiredService(STMinisteresService.class);
    }

    /**
     * Retourne le service de ldap postes
     *
     * @return
     */
    public static STPostesService getSTPostesService() {
        return ServiceUtil.getRequiredService(STPostesService.class);
    }

    /**
     * Retourne le service de ldap unités structurelles et directions
     *
     * @return
     */
    public static STUsAndDirectionService getSTUsAndDirectionService() {
        return ServiceUtil.getRequiredService(STUsAndDirectionService.class);
    }

    /**
     * Retourne le service de persistance
     *
     * @return
     */
    public static STPersistanceService getSTPersistanceService() {
        return ServiceUtil.getRequiredService(STPersistanceService.class);
    }

    /**
     * Retourne le service de sécurité par brute force
     *
     * @return
     */
    public static BruteforceSecurityService getBruteforceSecurityService() {
        return ServiceUtil.getRequiredService(BruteforceSecurityService.class);
    }

    /**
     * Retourne le service des profils utilisateur
     *
     * @return
     */
    public static STProfilUtilisateurService getSTProfilUtilisateurService() {
        return ServiceUtil.getRequiredService(STProfilUtilisateurService.class);
    }

    public static STCorbeilleService getCorbeilleService() {
        return ServiceUtil.getRequiredService(STCorbeilleService.class);
    }

    /**
     * Retourne le service de récupération des champs pour le suivi
     *
     * @return
     */
    public static RechercheChampService getRechercheChampService() {
        return ServiceUtil.getRequiredService(RechercheChampService.class);
    }

    /**
     * Retourne le service des fonctions
     *
     * @return
     */
    public static FonctionService getFonctionService() {
        return ServiceUtil.getRequiredService(FonctionService.class);
    }

    public static DownloadService getDownloadService() {
        return ServiceUtil.getRequiredService(DownloadService.class);
    }

    public static STTokenService getTokenService() {
        return ServiceUtil.getRequiredService(STTokenService.class);
    }

    public static STUserSearchService getSTUserSearchService() {
        return ServiceUtil.getRequiredService(STUserSearchService.class);
    }

    public static STVersionApplicationService getVersionService() {
        return ServiceUtil.getRequiredService(STVersionApplicationService.class);
    }

    public static WorkManager getWorkManager() {
        return ServiceUtil.getRequiredService(WorkManager.class);
    }
}
