package fr.dila.st.api.requeteur;

import java.util.Map;

/**
 * Interface pour ajouter des informations supplémentaires à un champ. Un nouveau type et des propriétés
 * supplémentaires.
 * 
 * @author jgomez
 * 
 */
public interface RequeteurAddInfoField {

	/**
	 * Retourne l'information du nouveau type
	 * 
	 * @return le nouveau type, qui permet de récupérer le bon template
	 */
	String getNewType();

	/**
	 * Retourne les propriétés supplémentaires liées au nouveau type du widget. Par exemple, le type du noeud pour un
	 * widget de newType orga.
	 * 
	 * @return Une map clé, valeur des propriétés supplémentaires
	 */
	Map<String, String> getProperties();

	/**
	 * Retourne le nom du widget
	 * 
	 * @return le nom du widget
	 */
	String getName();

	/**
	 * Retourne la nouvelle catégory du widget
	 * 
	 * @return la nouvelle catégorie du widget
	 */
	String getNewCategory();

}
