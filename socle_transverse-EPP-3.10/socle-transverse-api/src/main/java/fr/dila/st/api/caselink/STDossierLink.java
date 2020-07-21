package fr.dila.st.api.caselink;

import java.io.Serializable;

import fr.dila.cm.caselink.CaseLink;

/**
 * DossierLink Interface (herit CaseLink Interface)
 * 
 * @author ARN
 */
public interface STDossierLink extends CaseLink, Serializable {

	// *************************************************************
	// Distribution
	// *************************************************************
	/**
	 * Retourne la Mailbox de distribution du DossierLink
	 */
	String getDistributionMailbox();

	// *************************************************************
	// Dossier
	// *************************************************************
	/**
	 * Retourne l'identifiant technique du dossier.
	 * 
	 * @return Identifiant technique du dossier
	 */
	String getDossierId();

	// *************************************************************
	// Étapes de feuille de route
	// *************************************************************
	/**
	 * Retourne l'identifiant technique de l'étape en cours.
	 * 
	 * @return Identifiant technique de l'étape en cours
	 */
	String getRoutingTaskId();

	/**
	 * Renseigne l'identifiant technique de l'étape en cours.
	 * 
	 * @param Identifiant
	 *            technique de l'étape en cours
	 */
	void setRoutingTaskId(String routingTaskId);

	/**
	 * Retourne le type d'étape en cours.
	 * 
	 * @return Type d'étape en cours
	 */
	String getRoutingTaskType();

	/**
	 * Renseigne le type d'étape en cours.
	 * 
	 * @param routingTaskType
	 *            Type d'étape en cours
	 */
	void setRoutingTaskType(String routingTaskType);

	/**
	 * Retourne le libellé de l'étape en cours.
	 * 
	 * @return Libellé de l'étape en cours
	 */
	String getRoutingTaskLabel();

	/**
	 * Renseigne le libellé de l'étape en cours.
	 * 
	 * @param Libellé
	 *            de l'étape en cours
	 */
	void setRoutingTaskLabel(String routingTaskLabel);

	/**
	 * Retourne le libellé de la Mailbox de distribution (champ dénormalisé).
	 * 
	 * @return Libellé de la Mailbox de distribution
	 */
	String getRoutingTaskMailboxLabel();

	/**
	 * Renseigne le libellé de la Mailbox de distribution (champ dénormalisé).
	 * 
	 * @param Libellé
	 *            de la Mailbox de distribution
	 */
	void setRoutingTaskMailboxLabel(String routingTaskMailboxLabel);

	/**
	 * 
	 * Gets the CurrentStepIsMailSendProperty.
	 */
	Boolean getCurrentStepIsMailSendProperty();

	void setCurrentStepIsMailSendProperty(Boolean currentStepIsMailSendProperty);

	/**
	 * set the value of the property cslk:isRead
	 */
	void setReadState(Boolean isRead);

}
