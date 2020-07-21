package fr.dila.st.api.constant;

/**
 * Constantes des événements du socle transverse.
 * 
 * @author jtremeaux
 */
public final class STEventConstant {

	/**
	 * property comment de l'event
	 */
	public static final String	COMMENT_PROPERTY											= "comment";

	/**
	 * property category de l'event
	 */
	public static final String	CATEGORY_PROPERTY											= "category";

	// *************************************************************
	// Catégorie d'Événements
	// *************************************************************

	/**
	 * type d'événement lié au Bordereau
	 */
	public static final String	CATEGORY_BORDEREAU											= "Bordereau";

	/**
	 * type d'événement lié au Fond De Dossier
	 */
	public static final String	CATEGORY_FDD												= "FondDeDossier";

	/**
	 * type d'événement lié au Parapheur
	 */
	public static final String	CATEGORY_PARAPHEUR											= "Parapheur";

	/**
	 * type d'événement lié à la feuille de route
	 */
	public static final String	CATEGORY_FEUILLE_ROUTE										= "FeuilleRoute";

	/**
	 * type d'événement lié à la reprise de donnes
	 */
	public static final String	CATEGORY_REPRISE											= "Reprise";

	/**
	 * type d'événement lié au journal
	 */
	public static final String	CATEGORY_JOURNAL											= "Journal";

	/**
	 * type d'événement lié à l'espace d'administration
	 */
	public static final String	CATEGORY_ADMINISTRATION										= "Administration";

	// *************************************************************
	// Événements de l'audit trail
	// *************************************************************

	public static final String	DOSSIER_POUR_VALIDATION_PM									= "pourValidationRetourPM";
	public static final String	COMMENT_DOSSIER_POUR_VALIDATION_PM							= "label.journal.comment.pourValidationPM";

	/**
	 * événement : avis favorable pour l'étape de feuille de route liée au dossier
	 */
	public static final String	DOSSIER_AVIS_FAVORABLE										= "avisFavorable";

	/**
	 * commentaire : avis favorable pour l'étape de feuille de route liée au dossier
	 */
	public static final String	COMMENT_AVIS_FAVORABLE										= "label.journal.comment.avisFavorable";

	/**
	 * Événement de validation avec retour pour modification de l'étape en cours.
	 */
	public static final String	DOSSIER_VALIDER_RETOUR_MODIFICATION_EVENT					= "validerRetourModification";

	/**
	 * événement : avis défavorable pour l'étape de feuille de route liée au dossier
	 */
	public static final String	DOSSIER_AVIS_DEFAVORABLE									= "avisDeFavorable";

	/**
	 * commentaire : avis défavorable pour l'étape de feuille de route liée au dossier
	 */
	public static final String	COMMENT_AVIS_DEFAVORABLE									= "label.journal.comment.avisDefavorable";

	/**
	 * événement : avis rectificatif CE pour le dossier
	 */
	public static final String	DOSSIER_AVIS_RECTIFICATIF									= "avisRectificatif";

	/**
	 * commentaire : avis rectificatif CE pour le dossier
	 */
	public static final String	COMMENT_AVIS_RECTIFICATIF									= "label.journal.comment.avisRectificatif";

	/**
	 * événement : substitution de la feuille de route liée au dossier
	 */
	public static final String	DOSSIER_SUBSTITUER_FEUILLE_ROUTE							= "substituerFeuilleRoute";

	/**
	 * commentaire : substitution de la feuille de route liée au dossier
	 */
	public static final String	COMMENT_SUBSTITUER_FEUILLE_ROUTE							= "label.journal.comment.substitution";

	/**
	 * Événement de validation "non concerné" de l'étape en cours.
	 */
	public static final String	DOSSIER_VALIDER_NON_CONCERNE_EVENT							= "validerNonConcerne";

	/**
	 * Commentaire de l'événement de validation "non concerné" de l'étape en cours.
	 */
	public static final String	DOSSIER_VALIDER_NON_CONCERNE_COMMENT_PARAM					= "label.journal.comment.validerNonConcerne";

	/**
	 * événement : création du dossier
	 */
	public static final String	EVENT_DOSSIER_CREATION										= "dossierCreation";

	/**
	 * commentaire : création du dossier
	 */
	public static final String	COMMENT_DOSSIER_CREATION									= "label.journal.comment.createDossier";

	/**
	 * événement : mise à jour du bordereau
	 */
	public static final String	EVENT_BORDEREAU_UPDATE										= "bordereauUpdate";

	/**
	 * commentaire : mise à jour du dossier
	 */
	public static final String	COMMENT_BORDEREAU_UPDATE									= "label.journal.comment.updateBordereau";

	/**
	 * événement : déplacement d'une étape de la feuille de route
	 */
	public static final String	EVENT_FEUILLE_ROUTE_STEP_MOVE								= "feuilleRouteStepMove";

	/**
	 * commentaire : déplacement d'une étape de la feuille de route
	 */
	public static final String	COMMENT_FEUILLE_ROUTE_STEP_MOVE								= "label.journal.comment.moveStepRoute";

	/**
	 * événement : suppression d'une étape de la feuille de route
	 */
	public static final String	EVENT_FEUILLE_ROUTE_STEP_DELETE								= "feuilleRouteStepDelete";

	/**
	 * commentaire : suppression d'une étape de la feuille de route
	 */
	public static final String	COMMENT_FEUILLE_ROUTE_STEP_DELETE							= "label.journal.comment.deleteStepRoute";

	/**
	 * événement : modification d'une étape de la feuille de route
	 */
	public static final String	EVENT_FEUILLE_ROUTE_STEP_UPDATE								= "feuilleRouteStepUpdate";

	/**
	 * commentaire : modification d'une étape de la feuille de route
	 */
	public static final String	COMMENT_FEUILLE_ROUTE_STEP_UPDATE							= "label.journal.comment.updateStepRoute";

	/**
	 * événement : creation d'une étape de la feuille de route
	 */
	public static final String	EVENT_FEUILLE_ROUTE_STEP_CREATE								= "feuilleRouteStepCreate";

	/**
	 * commentaire : creation d'une étape de la feuille de route
	 */
	public static final String	COMMENT_FEUILLE_ROUTE_STEP_CREATE							= "label.journal.comment.createStepRoute";
	/**
	 * événement : envoi mail dossier
	 */
	public static final String	EVENT_ENVOI_MAIL_DOSSIER									= "envoiMailDossier";

	/**
	 * commentaire : envoi mail dossier
	 */
	public static final String	COMMENT_ENVOI_MAIL_DOSSIER									= "label.journal.comment.envoiMail";

	/**
	 * événement : archivage dossier
	 */
	public static final String	EVENT_ARCHIVAGE_DOSSIER										= "archivageDossier";

	/**
	 * commentaire : archivage dossier
	 */
	public static final String	COMMENT_ARCHIVAGE_DOSSIER									= "label.journal.comment.archivage";
	
	/**
	 * commentaire : suppression dossier
	 */
	public static final String	COMMENT_SUPPRESSION_DOSSIER									= "label.journal.comment.suppression";

	/**
	 * événement : archivage dossier
	 */
	public static final String	EVENT_EXPORT_ZIP_DOSSIER									= "exportZipDossier";

	/**
	 * commentaire : archivage dossier
	 */
	public static final String	COMMENT_EXPORT_ZIP_DOSSIER									= "label.journal.comment.exportZip";
	
	/**
	 * commentaire : copie fdr depuis un dossier
	 */
	public static final String	COMMENT_COPIE_FDR_DEPUIS_DOSSIER							= "label.journal.comment.copieFdrDossier";
	
	/**
	 * événement : copie fdr depuis un dossier
	 */
	public static final String	EVENT_COPIE_FDR_DEPUIS_DOSSIER								= "copieFdrDossier";

	// *************************************************************
	// Événements de Nuxeo
	// *************************************************************
	/**
	 * Événement de changement du document en cours.
	 */
	public static final String	CURRENT_DOCUMENT_CHANGED_EVENT								= "currentDocumentChanged";

	// *************************************************************
	// Événements de la gestion des modèles de feuille de route
	// *************************************************************
	/**
	 * Événement de demande de validation d'un modèle de feuille de route (avant).
	 */
	public static final String	BEFORE_REQUEST_VALIDATE_FEUILLE_ROUTE						= "beforeRequestValidateFeuilleRoute";

	/**
	 * Événement de demande de validation d'un modèle de feuille de route (après).
	 */
	public static final String	AFTER_REQUEST_VALIDATE_FEUILLE_ROUTE						= "afterRequestValidateFeuilleRoute";

	/**
	 * Événement d'annulation de demande de validation d'un modèle de feuille de route (avant).
	 */
	public static final String	BEFORE_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE				= "beforeCancelRequestValidateFeuilleRoute";

	/**
	 * Événement d'annulation de demande de validation d'un modèle de feuille de route (après).
	 */
	public static final String	AFTER_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE					= "afterCancelRequestValidateFeuilleRoute";

	/**
	 * Événement d'invalidation d'un modèle de feuille de route (avant).
	 */
	public static final String	BEFORE_INVALIDATE_FEUILLE_ROUTE								= "beforeInvalidateFeuilleRoute";

	/**
	 * Événement d'invalidation d'un modèle de feuille de route (après).
	 */
	public static final String	AFTER_INVALIDATE_FEUILLE_ROUTE								= "afterInvalidateFeuilleRoute";

	// *************************************************************
	// Événements de l'organigramme
	// *************************************************************
	/**
	 * Événement de création d'un poste dans l'organigramme.
	 */
	public static final String	POSTE_CREATED_EVENT											= "posteCreated";

	/**
	 * Événement de mise à jour d'un poste dans l'organigramme.
	 */
	public static final String	POSTE_UPDATED_EVENT											= "posteUpdated";

	/**
	 * Événement de création d'un noeud dans l'organigramme.
	 */
	public static final String	NODE_CREATED_EVENT											= "node_created";
	public static final String	NODE_CREATED_EVENT_COMMENT									= "label.journal.comment.node.created";

	/**
	 * Événement de delete d'un noeud dans l'organigramme.
	 */
	public static final String	NODE_DELETED_EVENT											= "node_deleted";
	public static final String	NODE_DELETED_EVENT_COMMENT									= "label.journal.comment.node.deleted";

	/**
	 * Événement de modification d'un noeud dans l'organigramme.
	 */
	public static final String	NODE_MODIFIED_EVENT											= "node_modified";
	public static final String	NODE_MODIFIED_EVENT_COMMENT									= "label.journal.comment.node.modified";

	/**
	 * Événement de delete d'un noeud dans l'organigramme.
	 */
	public static final String	NODE_ACTIVATION_EVENT										= "node_activation";
	public static final String	NODE_ACTIVATION_EVENT_COMMENT								= "label.journal.comment.node.activation";

	/**
	 * Événement de delete d'un noeud dans l'organigramme.
	 */
	public static final String	NODE_DESACTIVATION_EVENT									= "node_desactivation";
	public static final String	NODE_DESACTIVATION_EVENT_COMMENT							= "label.journal.comment.node.desactivation";

	// *************************************************************
	// Événements de la distribution des dossiers
	// *************************************************************
	/**
	 * Événement de création d'un poste dans l'organigramme.
	 */
	public static final String	AFTER_SUBSTITUTION_FEUILLE_ROUTE							= "afterSubstitutionFeuilleRoute";

	// *************************************************************
	// Événements du service de jeton
	// *************************************************************
	/**
	 * Événement de création d'un poste dans l'organigramme.
	 */
	public static final String	AFTER_CREATION_JETON										= "afterCreationJeton";

	/**
	 * Parametre webservice de l'evenement AFTER_CREATION_JETON
	 */
	public static final String	AFTER_CREATION_JETON_PARAM_WEBSERVICE						= "webservice";

	/**
	 * Parametre webservice de l'evenement AFTER_CREATION_JETON
	 */
	public static final String	AFTER_CREATION_JETON_PARAM_TYPE_MODIFICATION				= "typeModification";

	/**
	 * Parametre owner de l'evenement AFTER_CREATION_JETON
	 */
	public static final String	AFTER_CREATION_JETON_PARAM_OWNER							= "owner";

	/**
	 * Parametre documentId de l'évenement AFTER_CREATION_JETON
	 */
	public static final String	AFTER_CREATION_JETON_PARAM_DOC_ID							= "documentId";

	/**
	 * Evènement qui déclenche une création de jeton
	 */
	public static final String	VALIDATED_STEP_EVENT_FOR_JETON								= "validatedStepEventForJeton";

	/**
	 * Paramètre idOwner pour la création d'un jeton
	 */
	public static final String	ID_OWNER_FOR_JETON											= "idOwner";

	/**
	 * Paramètre typeWebService pour la création d'un jeton
	 */
	public static final String	TYPE_WS_FOR_JETON											= "typeWS";

	/**
	 * Paramètre numeroDossier pour la création d'un jeton
	 */
	public static final String	NUMERO_DOSSIER_FOR_JETON									= "numeroDossier";

	/**
	 * Paramètre id complémentaire pour la création d'un jeton
	 */
	public static final String	AFTER_CREATION_JETON_PARAM_IDS_COMPLEMENTAIRES				= "idsComplementaires";

	// *************************************************************
	// Paramètres des événements inline
	// *************************************************************
	/**
	 * Paramètre de contexte lors de la distribution d'un dossier pour l'envoi ou non de mails
	 */
	public static final String	OPERATION_SEND_MAIL_KEY										= "operation.send.mail.key";

	/**
	 * Document avant le changement du document en cours.
	 */
	public static final String	OLD_DOCUMENT_EVENT_PARAM									= "oldDocument";

	/**
	 * Document après le changement du document en cours.
	 */
	public static final String	NEW_DOCUMENT_EVENT_PARAM									= "newDocument";

	/**
	 * Identifiant technique du noeud de l'organigramme.
	 */
	public static final String	ORGANIGRAMME_NODE_ID_EVENT_PARAM							= "nodeId";

	/**
	 * Libellé du noeud de l'organigramme.
	 */
	public static final String	ORGANIGRAMME_NODE_LABEL_EVENT_PARAM							= "nodeLabel";

	/**
	 * Type de création de l'instance de feuille de route.
	 */
	public static final String	DOSSIER_DISTRIBUTION_SUBSTITUTION_ROUTE_TYPE_EVENT_PARAM	= "typeCreation";

	/**
	 * Instance de feuille de route avant la substitution.
	 */
	public static final String	DOSSIER_DISTRIBUTION_OLD_ROUTE_EVENT_PARAM					= "oldRoute";

	/**
	 * Instance de feuille de route après la substitution.
	 */
	public static final String	DOSSIER_DISTRIBUTION_NEW_ROUTE_EVENT_PARAM					= "newRoute";

	/**
	 * évènement qui permet l'envoi d'un mail aux membres du poste lorsqu'on arrive a dans l'étape de la feuille route.
	 */
	public static final String	SEND_MAIL_NOTIFICATION										= "sendMailNotification";

	/**
	 * Parametre mailboxId de l'event SEND_MAIL_NOTIFICATION
	 */
	public static final String	SEND_MAIL_NOTIFICATION_MAILBOX_ID							= "sendMailNotificationMailboxId";

	/**
	 * Parametre Objet de l'event SEND_MAIL_NOTIFICATION
	 */
	public static final String	SEND_MAIL_NOTIFICATION_OBJET								= "sendMailNotificationObjet";

	/**
	 * Parametre Texte de l'event SEND_MAIL_NOTIFICATION
	 */
	public static final String	SEND_MAIL_NOTIFICATION_TEXTE								= "sendMailNotificationTexte";

	public static final String	SEND_MAIL_AFTER_DISTRIBUTION_NOTIFICATION					= "sendMailAfterDistributionNotification";

	/**
	 * création d'un fichier dans une arborescence de document
	 */
	public static final String	BORDEREAU_UPDATE											= "bordereauUpdate";

	/**
	 * Paramètre d'un évènement : Identifiant de la mailbox de l'étape.
	 */
	public static final String	STEP_MAILBOX_EVENT_PARAM									= "stepMailboxId";

	/**
	 * Paramètre d'un évènement : Identifiant du type de l'étape.
	 */
	public static final String	STEP_TYPE_EVENT_PARAM										= "stepTypeId";

	// *************************************************************
	// Evénements des batchs
	// *************************************************************
	public static final String	LANCEUR_BATCH_EVENT											= "lanceurGeneralBatchEvent";

	public static final String	UNLOCK_BATCH_EVENT											= "unlockBatchEvent";

	public static final String	WS_NOTIFICATION_EVENT										= "wsNotificationEvent";

	public static final String	SEND_DAILY_REMIND_CHANGE_PASS_EVENT							= "dailyReminderChangePassEvent";

	public static final String	BATCH_EVENT_PROPERTY_PARENT_ID								= "eventPropertyParentId";

	public static final String	BATCH_EVENT_PURGE_CALENDRIER_BATCH							= "purgeCalendrierBatch";
	
	public static final String	BATCH_EVENT_PURGE_TENTATIVES_CONNEXION						= "purgeTentativesConnexionEvent";

	public static final String UNLOCK_ORGANIGRAMME_BATCH_EVENT 								= "unlockOrganigrammeBatchEvent";

	/**
	 * utility class
	 */
	private STEventConstant() {
		// do nothing
	}
}
