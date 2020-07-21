package fr.dila.st.core.service;

import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.EventServiceAdmin;
import org.nuxeo.ecm.core.trash.TrashService;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryFactory;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.ecm.platform.versioning.api.VersioningManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.st.api.service.CorbeilleService;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.api.service.CleanupService;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.DelegationService;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.STExportService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.api.service.STRequeteurWidgetGeneratorService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.util.ServiceUtil;

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
		return ServiceUtil.getService(PathSegmentService.class);
	}

	/**
	 * Retourne le service ProfileService.
	 * 
	 * @return service ProfileService
	 */
	public static ProfileService getProfileService() {
		return ServiceUtil.getService(ProfileService.class);
	}

	/**
	 * Retourne le service UserManager.
	 * 
	 * @return service UserManager
	 */
	public static UserManager getUserManager() {
		return ServiceUtil.getService(UserManager.class);
	}

	/**
	 * Retourne le service UserWorkspace.
	 * 
	 * @return service UserWorkspace
	 */
	public static UserWorkspaceService getUserWorkspaceService() {
		return ServiceUtil.getLocalService(UserWorkspaceService.class);
	}

	/**
	 * Retourne le service RepositoryManager.
	 * 
	 * @return service RepositoryManager
	 */
	public static RepositoryManager getRepositoryManager() {
		return ServiceUtil.getService(RepositoryManager.class);
	}

	/**
	 * Retourne le service VersioningManager.
	 * 
	 * @return service VersioningManager
	 */
	public static VersioningManager getVersioningManager() {
		return ServiceUtil.getLocalService(VersioningManager.class);
	}

	/**
	 * Retourne le service CaseManagementDocumentTypeService.
	 * 
	 * @return service CaseManagementDocumentTypeService
	 */
	public static CaseManagementDocumentTypeService getCaseManagementDocumentTypeService() {
		return ServiceUtil.getService(CaseManagementDocumentTypeService.class);
	}

	/**
	 * Retourne le service CaseDistributionService
	 * 
	 * @return service CaseDistributionService
	 */
	public static CaseDistributionService getCaseDistributionService() {
		return ServiceUtil.getService(CaseDistributionService.class);
	}

	/**
	 * Retourne le service DirectoryService.
	 * 
	 * @return service DirectoryService
	 */
	public static DirectoryService getDirectoryService() {
		return ServiceUtil.getLocalService(DirectoryService.class);
	}

	/**
	 * Retourne le service EventServiceAdmin.
	 * 
	 * @return service EventServiceAdmin
	 */
	public static EventServiceAdmin getEventServiceAdmin() {
		return ServiceUtil.getLocalService(EventServiceAdmin.class);
	}

	/**
	 * Retourne le service ConfigService.
	 * 
	 * @return service ConfigService
	 */
	public static ConfigService getConfigService() {
		return ServiceUtil.getService(ConfigService.class);
	}

	/**
	 * Retourne le service EventProducer.
	 * 
	 * @return service EventProducer
	 */
	public static EventProducer getEventProducer() {
		return ServiceUtil.getService(EventProducer.class);
	}

	/**
	 * Retourne le service LDAPDirectoryFactory.
	 * 
	 * @return service LDAPDirectoryFactory
	 */
	public static LDAPDirectoryFactory getLDAPDirectoryFactory() {
		LDAPDirectoryFactory factory = (LDAPDirectoryFactory) Framework.getRuntime().getComponent(
				LDAPDirectoryFactory.NAME);

		return factory;
	}

	/**
	 * Retourne le service Organigramme.
	 * 
	 * @return service Organigramme
	 */
	public static OrganigrammeService getOrganigrammeService() {
		return ServiceUtil.getService(OrganigrammeService.class);
	}

	/**
	 * Retourne le service STLockService.
	 * 
	 * @return service STLockService
	 */
	public static STLockService getSTLockService() {
		return ServiceUtil.getService(STLockService.class);
	}

	/**
	 * Retourne le service Journal.
	 * 
	 * @return service Journal
	 */
	public static JournalService getJournalService() {
		return ServiceUtil.getService(JournalService.class);
	}

	/**
	 * Retourne le service Mailbox.
	 * 
	 * @return service Mailbox
	 */
	public static MailboxService getMailboxService() {
		return ServiceUtil.getService(MailboxService.class);
	}

	/**
	 * Retourne le service Security.
	 * 
	 * @return service Security
	 */
	public static SecurityService getSecurityService() {
		return ServiceUtil.getService(SecurityService.class);
	}

	/**
	 * Retourne le service TypeManager.
	 * 
	 * @return service TypeManager
	 */
	public static TypeManager getTypeManager() {
		return ServiceUtil.getService(TypeManager.class);
	}

	/**
	 * Retourne le service TrashService.
	 * 
	 * @return service Mail
	 */
	public static TrashService getTrashService() {
		return ServiceUtil.getService(TrashService.class);
	}

	/**
	 * Retourne le service Mail du socle.
	 * 
	 * @return service Mail
	 */
	public static STMailService getSTMailService() {
		return ServiceUtil.getService(STMailService.class);
	}

	/**
	 * 
	 * Retourne le service de vocabulaire.
	 * 
	 * @return service Vocabulaire
	 */

	public static VocabularyService getVocabularyService() {
		return ServiceUtil.getService(VocabularyService.class);
	}

	/**
	 * Retourne le service STUserService.
	 * 
	 * @return service STUserService
	 */
	public static STUserService getSTUserService() {
		return ServiceUtil.getService(STUserService.class);
	}

	/**
	 * Retourne le service STParametreService.
	 * 
	 * @return service STParametreService
	 */
	public static STParametreService getSTParametreService() {
		return ServiceUtil.getService(STParametreService.class);
	}

	/**
	 * Retourn le service Alerte
	 * 
	 * @return service Alert
	 */
	public static STAlertService getAlertService() {
		STAlertService service = ServiceUtil.getService(STAlertService.class);
		return service;
	}

	/**
	 * 
	 * Retourne le service requeteur
	 * 
	 * @return service RequeteurService
	 */
	public static RequeteurService getRequeteurService() {
		RequeteurService service = ServiceUtil.getService(RequeteurService.class);
		return service;
	}

	/**
	 * Retourne le service de jointure du projet,
	 * 
	 * @return service JointureService
	 */
	public static JointureService getJointureService() {
		JointureService service = ServiceUtil.getService(JointureService.class);
		return service;
	}

	/**
	 * Retourne le service Delegation.
	 * 
	 * @return service Delegation
	 */
	public static DelegationService getDelegationService() {
		DelegationService service = ServiceUtil.getService(DelegationService.class);
		return service;
	}

	/**
	 * Retourne le service STAuditEventsService.
	 * 
	 * @return service STAuditEventsService
	 */
	public static STAuditEventsService getSTAuditEventsService() {
		STAuditEventsService service = ServiceUtil.getService(STAuditEventsService.class);
		return service;
	}

	/**
	 * Retourne le service de suivi des batchs.
	 * 
	 * @return service SuiviBatchService
	 */
	public static SuiviBatchService getSuiviBatchService() {
		SuiviBatchService service = ServiceUtil.getService(SuiviBatchService.class);
		return service;
	}

	/**
	 * Retourne le service de notification de suivi des batchs
	 * 
	 * @return NotificationsSuiviBatchsService
	 */
	public static NotificationsSuiviBatchsService getNotificationsSuiviBatchsService() {
		NotificationsSuiviBatchsService service = ServiceUtil.getService(NotificationsSuiviBatchsService.class);
		return service;
	}

	/**
	 * Retourne le service EtatApplicationService
	 * 
	 * @return EtatApplicationService
	 */
	public static EtatApplicationService getEtatApplicationService() {
		EtatApplicationService service = ServiceUtil.getService(EtatApplicationService.class);
		return service;
	}

	/**
	 * Retourne le service MailboxCreator
	 * 
	 * @return MailboxCreator
	 */
	public static MailboxCreator getMailboxCreator() {
		MailboxCreator service = ServiceUtil.getService(MailboxCreator.class);
		return service;
	}

	/**
	 * Retourne le service de génération des widgets du requêteur
	 * 
	 * @return {@link STRequeteurWidgetGeneratorService}
	 */
	public static STRequeteurWidgetGeneratorService getSTRequeteurWidgetGeneratorService() {
		return ServiceUtil.getService(STRequeteurWidgetGeneratorService.class);
	}

	/**
	 * Retourne le service pour supprimer des documents deleted
	 * 
	 * @return
	 */
	public static CleanupService getCleanupService() {
		return ServiceUtil.getService(CleanupService.class);
	}

	/**
	 * 
	 * Retourne le service de jeton
	 * 
	 * @return service Jeton
	 */
	public static JetonService getJetonService() {
		return ServiceUtil.getService(JetonService.class);
	}

	public static EventService getEventService() {
		return ServiceUtil.getLocalService(EventService.class);
	}

	public static STExportService getSTExportService() {
		return ServiceUtil.getLocalService(STExportService.class);
	}

	/**
	 * Retourne le service de ldap gourvernement
	 * 
	 * @return
	 */
	public static STGouvernementService getSTGouvernementService() {
		return ServiceUtil.getLocalService(STGouvernementService.class);
	}

	/**
	 * Retourne le service de ldap ministères
	 * 
	 * @return
	 */
	public static STMinisteresService getSTMinisteresService() {
		return ServiceUtil.getLocalService(STMinisteresService.class);
	}

	/**
	 * Retourne le service de ldap postes
	 * 
	 * @return
	 */
	public static STPostesService getSTPostesService() {
		return ServiceUtil.getLocalService(STPostesService.class);
	}

	/**
	 * Retourne le service de ldap unités structurelles et directions
	 * 
	 * @return
	 */
	public static STUsAndDirectionService getSTUsAndDirectionService() {
		return ServiceUtil.getLocalService(STUsAndDirectionService.class);
	}

	/**
	 * Retourne le service de persistance
	 * 
	 * @return
	 */
	public static STPersistanceService getSTPersistanceService() {
		return ServiceUtil.getService(STPersistanceService.class);
	}

	public static CorbeilleService getCorbeilleService() {
		return ServiceUtil.getService(CorbeilleService.class);
	}

}
