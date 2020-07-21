package fr.dila.solonepp.api.domain.evenement;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des objets métiers événement.
 * 
 * @author jtremeaux
 */
public interface Evenement extends Serializable {
	/**
	 * Retourne le modèle de document.
	 */
	DocumentModel getDocument();

	/**
	 * Retourne le titre de l'événement.
	 * 
	 * @return Titre de l'événement
	 */
	String getTitle();

	/**
	 * Renseigne le titre de l'événement.
	 * 
	 * @param title
	 *            Titre de l'événement
	 */
	void setTitle(String title);

	// *************************************************************
	// Propriétés du document événement.
	// *************************************************************
	/**
	 * Retourne l'identifiant technique du type d'événement (vocabulaire).
	 * 
	 * @return Identifiant technique du type d'événement (vocabulaire)
	 */
	String getTypeEvenement();

	/**
	 * Renseigne l'identifiant technique du type d'événement (vocabulaire).
	 * 
	 * @param typeEvenement
	 *            Identifiant technique du type d'événement (vocabulaire)
	 */
	void setTypeEvenement(String typeEvenement);

	/**
	 * Retourne l'identifiant technique de l'événement parent (uniquement pour
	 * les événements non créateurs)
	 * 
	 * @return Identifiant technique de l'événement parent (uniquement pour les
	 *         événements non créateurs)
	 */
	String getEvenementParent();

	/**
	 * Renseigne l'identifiant technique de l'événement parent (uniquement pour
	 * les événements non créateurs)
	 * 
	 * @param evenementParent
	 *            Identifiant technique de l'événement parent (uniquement pour
	 *            les événements non créateurs)
	 */
	void setEvenementParent(String evenementParent);

	/**
	 * Retourne l'identifiant technique du dossier.
	 * 
	 * @return Identifiant technique du dossier
	 */
	String getDossier();

	/**
	 * Renseigne l'identifiant technique du dossier.
	 * 
	 * @param Dossier
	 *            Identifiant technique du dossier
	 */
	void setDossier(String dossier);

	/**
	 * Retourne l'identifiant technique de l'émetteur.
	 * 
	 * @return Identifiant technique de l'émetteur
	 */
	String getEmetteur();

	/**
	 * Renseigne l'identifiant technique de l'émetteur.
	 * 
	 * @param Emetteur
	 *            Identifiant technique de l'émetteur
	 */
	void setEmetteur(String emetteur);

	/**
	 * Retourne l'identifiant technique du destinataire.
	 * 
	 * @return Identifiant technique du destinataire
	 */
	String getDestinataire();

	/**
	 * Renseigne l'identifiant technique du destinataire.
	 * 
	 * @param Destinataire
	 *            Identifiant technique du destinataire
	 */
	void setDestinataire(String destinataire);

	/**
	 * Retourne les identifiants techniques des destinataires en copie.
	 * 
	 * @return Identifiants techniques des destinataires en copie
	 */
	List<String> getDestinataireCopie();

	/**
	 * Renseigne les identifiants techniques des destinataires en copie.
	 * 
	 * @param destinataireCopie
	 *            Identifiants techniques des destinataires en copie
	 */
	void setDestinataireCopie(List<String> destinataireCopie);

	/**
	 * Retourne les identifiants techniques des destinataires en copie concaténé
	 * 
	 * @return Identifiants techniques des destinataires en copie
	 */
	String getDestinataireCopieConcat();

	/**
	 * Renseigne les identifiants techniques des destinataires en copie
	 * concaténé
	 * 
	 * @param destinataireCopie
	 *            Identifiants techniques des destinataires en copie
	 */
	void setDestinataireCopieConcat(String destinataireCopieConcat);

	/**
	 * Retourne l'état en alerte de la branche (POSEE, LEVEE).
	 * 
	 * @return État en alerte de la branche (POSEE, LEVEE)
	 */
	String getBrancheAlerte();

	/**
	 * Renseigne l'état en alerte de la branche (POSEE, LEVEE).
	 * 
	 * @param brancheAlerte
	 *            Etat en alerte de la branche (POSEE, LEVEE)
	 */
	void setBrancheAlerte(String brancheAlerte);

	// *************************************************************
	// Propriétés calculées sur l'état du cycle de vie.
	// *************************************************************
	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie init.
	 * 
	 * @return État du cycle de vie init
	 */
	boolean isEtatInit();

	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie brouillon.
	 * 
	 * @return État du cycle de vie brouillon
	 */
	boolean isEtatBrouillon();

	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie publié.
	 * 
	 * @return État du cycle de vie publié
	 */
	boolean isEtatPublie();

	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie en instance.
	 * 
	 * @return État du cycle de vie en instance
	 */
	boolean isEtatInstance();

	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie en attente de
	 * validation.
	 * 
	 * @return État du cycle de vie en attente de validation
	 */
	boolean isEtatAttenteValidation();

	/**
	 * Retourne vrai si l'événement est à l'état du cycle de vie annulé.
	 * 
	 * @return État du cycle de vie annulé
	 */
	boolean isEtatAnnule();

	void setIdEvenement(String idEvenement);

	String getIdEvenement();

	/**
	 * Retourne l'identifiant technique du dossier.
	 * 
	 * @return Identifiant technique du dossier
	 */
	String getDossierPrecedent();

	/**
	 * Renseigne l'identifiant technique du dossier.
	 * 
	 * @param Dossier
	 *            Identifiant technique du dossier
	 */
	void setDossierPrecedent(String dossierPrecedent);
}
