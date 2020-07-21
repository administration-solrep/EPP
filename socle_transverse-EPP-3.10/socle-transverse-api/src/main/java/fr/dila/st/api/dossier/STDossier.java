package fr.dila.st.api.dossier;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.cm.cases.Case;

/**
 * Interface des dossiers commune au socle transverse.
 * 
 * @author jtremeaux
 */
public interface STDossier extends Case {
	enum DossierState {
		init, running, done
	}

	enum DossierTransition {
		toRunning, toDone, backToRunning
	}

	/**
	 * Retourne l'identifiant technique de la dernière feuille de route lancée sur ce dossier.
	 * 
	 * @return Identifiant technique de la dernière feuille de route lancée sur ce dossier
	 */
	String getLastDocumentRoute();

	/**
	 * Renseigne l'identifiant technique de la dernière feuille de route lancée sur ce dossier.
	 * 
	 * @param routeDocId
	 *            Identifiant technique de la dernière feuille de route lancée sur ce dossier
	 */
	void setLastDocumentRoute(String routeDocId);

	/**
	 * Retourne vrai si le dossier est à l'état initial.
	 * 
	 * @return Vrai si le dossier est à l'état initial
	 * @throws ClientException
	 */
	boolean isInit() throws ClientException;

	/**
	 * Retourne vrai si le dossier est à l'état en cours.
	 * 
	 * @return Vrai si le dossier est à l'état en cours
	 * @throws ClientException
	 */
	boolean isRunning() throws ClientException;

	/**
	 * Retourne vrai si le dossier est à l'état terminé.
	 * 
	 * @return Vrai si le dossier est à l'état terminé
	 * @throws ClientException
	 */
	boolean isDone() throws ClientException;

	/**
	 * Retourne si un dossier a une feuille de route associé
	 */
	Boolean hasFeuilleRoute();
}
