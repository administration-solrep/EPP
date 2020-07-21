package fr.dila.st.api.recherche;

import java.io.Serializable;

/**
 * 
 * ATTENTION : cette classe ne correspond pas à ce qui était prévu au début, Une recherche correspond au contenu d'un
 * panneau dans les écrans recherche de Réponses et recherche de dossier simple de Réponses. elle va être renommé en
 * RechercheConfiguration
 * 
 * @author jgomez
 * 
 */
// TODO: Voir les champs qui ne servent à aucun des projets et supprimmer.
public interface Recherche extends Serializable {

	@Deprecated
	String getType();

	/**
	 * Retourne un texte qui est affiché en haut du panneau de recherche
	 * 
	 * @return
	 */
	String getMode();

	/**
	 * Retourne le layout qui correspond au panneau
	 * 
	 * @return
	 */
	String getLayoutName();

	/**
	 * Sans doute déprécié
	 * 
	 * @return
	 */
	String getContentViewName();

	@Deprecated
	String getTargetResourceName();

	/**
	 * Retourne le nom de la requête que doit effectuer cette requête
	 * 
	 * @return le nom de la requte
	 */
	String getRequeteName();

	/**
	 * Retourne un identifiant unique pour cette recherche
	 * 
	 * @return
	 */
	String getRechercheName();

	/**
	 * Retourne vrai si la recherche doit être replié par défaut
	 * 
	 * @return l'état replié ou non du panneau
	 */
	boolean getIsFolded();

	/**
	 * Met un place le paramêtre folded qui décrit le comportement de repli par défaut du panneau
	 * 
	 * @param folded
	 */
	void setIsFolded(boolean folded);

}
