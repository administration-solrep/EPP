package fr.dila.st.api.constant;

/**
 * Liste des fonctions unitaires du socle transverse. Ces fonctions déterminent la possibilité de cliquer sur un bouton,
 * afficher un menu, accéder à un document où à une vue.
 * 
 * @author jtremeaux
 */
public class STBaseFunctionConstant {
	/**
	 * Modification sur la feuille de route.
	 */
	public static final String	ROUTE_MANAGERS								= "routeManagers";

	/**
	 * Gestion des utilisateurs : création.
	 */
	public static final String	UTILISATEUR_CREATOR							= "UtilisateurCreator";

	/**
	 * Gestion des utilisateurs : mise à jour.
	 */
	public static final String	UTILISATEUR_UPDATER							= "UtilisateurUpdater";

	/**
	 * Gestion des utilisateurs du ministère : mise à jour.
	 */
	public static final String	UTILISATEUR_MINISTERE_UPDATER				= "UtilisateurMinistereUpdater";

	/**
	 * Gestion des utilisateurs : suppression.
	 */
	public static final String	UTILISATEUR_DELETER							= "UtilisateurDeleter";

	/**
	 * Gestion des utilisateurs du ministère : suppression.
	 */
	public static final String	UTILISATEUR_MINISTERE_DELETER				= "UtilisateurMinistereDeleter";

	/**
	 * Gestion des profils : création.
	 */
	public static final String	PROFIL_CREATOR								= "ProfilCreator";

	/**
	 * Gestion des profils : mise à jour.
	 */
	public static final String	PROFIL_UPDATER								= "ProfilUpdater";

	/**
	 * Gestion des profils : suppression.
	 */
	public static final String	PROFIL_DELETER								= "ProfilDeleter";

	/**
	 * Gestion des feuilles de route : administrateur ministériel.
	 */
	public static final String	FEUILLE_DE_ROUTE_MODEL_UDPATER				= "FDRModelUpdater";

	/**
	 * Gestion des feuilles de route : administrateur fonctionnel.
	 */
	public static final String	FEUILLE_DE_ROUTE_MODEL_VALIDATOR			= "FDRModelValidator";

	/**
	 * Gestion des feuilles de route : redémarrage d'une instance de feuille de route.
	 */
	public static final String	FEUILLE_DE_ROUTE_INSTANCE_RESTARTER			= "FDRInstanceRestarter";

	/**
	 * Organigramme : Admin unlocker
	 */
	public static final String	ORGANIGRAMME_ADMIN_UNLOCKER					= "OrganigrammeAdminUnlocker";

	/**
	 * Accès à l'interface
	 */
	public static final String	INTERFACE_ACCESS							= "InterfaceAccess";

	/**
	 * Nom du groupe "Administrateur fonctionnel"
	 */
	public static final String	ADMIN_FONCTIONNEL_GROUP_NAME				= "Administrateur fonctionnel";

	/**
	 * Nom du groupe "Administrateur ministeriel"
	 */
	public static final String	ADMIN_MINISTERIEL_GROUP_NAME				= "Administrateur ministériel";

	/**
	 * Profil SGG.
	 */
	public static final String	PROFIL_SGG									= "ProfilSGG";

	public static final String	ASSEMBLEES_PARLEMENTAIRES_READER			= "AssembleesParlementairesReader";

	/**
	 * Autorise la sélection d'un poste d'un autre ministère
	 */
	public static final String	ALLOW_ADD_POSTE_ALL_MINISTERE				= "AllowAddPosteAllMinistere";

	/**
	 * Email : réception des méls destinés aux administrateurs fonctionnels.
	 */
	public static final String	ADMIN_FONCTIONNEL_EMAIL_RECEIVER			= "AdminFonctionnelEmailReceiver";

	/**
	 * Consultation du journal d'administration : reader
	 */
	public static final String	ADMINISTRATION_JOURNAL_READER				= "AdministrationJournalReader";

	/**
	 * Consultation des profils : reader
	 */
	public static final String	ADMINISTRATION_PROFIL_READER				= "ProfilReader";

	/**
	 * Consultation des utilisateurs : reader
	 */
	public static final String	ADMINISTRATION_UTILISATEUR_READER			= "UtilisateurReader";

	/**
	 * Administration paramètres application : reader
	 */
	public static final String	ADMINISTRATION_PARAMETRE_APPLICATION_READER	= "AdministrationParamReader";

	/**
	 * Gestion des batchs : lecture.
	 */
	public static final String	BATCH_READER								= "BatchSuiviReader";

	/**
	 * Supervision des connexions des utilisateurs : reader
	 */
	public static final String	ADMINISTRATION_UTILISATEUR_SUPERVISION		= "EspaceSupervisionReader";

	/**
	 * Organigramme : droit de mise à jour
	 */
	public static final String	ORGANIGRAMME_UPDATER						= "OrganigrammeUpdater";

	/**
	 * Organigramme : droit de mise à jour
	 */
	public static final String	ORGANIGRAMME_READER							= "OrganigrammeReader";

	/**
	 * Organigramme : droit de mise à jour ministère uniquement
	 */
	public static final String	ORGANIGRAMME_MINISTERE_UPDATER				= "OrganigrammeMinistereUpdater";

	/**
	 * utility class
	 */
	protected STBaseFunctionConstant() {
		// do nothing
	}
}
