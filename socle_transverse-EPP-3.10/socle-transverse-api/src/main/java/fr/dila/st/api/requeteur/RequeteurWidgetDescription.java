package fr.dila.st.api.requeteur;

import java.util.Map;

/**
 * La description d'un widget du requêteur. L'interface contient toutes les informations utiles pour générer la
 * définition d'un widget nuxéo.
 * 
 * @author jgomez
 * 
 */
public interface RequeteurWidgetDescription {

	/**
	 * Récupère la catégorie à laquelle appartient le widget. Cette catégorie sert ensuite à regrouper les widgets au
	 * niveau de l'UI.
	 * 
	 * @return la catégorie
	 */
	String getCategory();

	/**
	 * Met en place la catégorie du widget.
	 * 
	 * @param category
	 *            la catégorie du widget.
	 */
	void setCategory(String category);

	/**
	 * Met en place le nom du widget. (sous la forme q.qu:nomAuteur)
	 * 
	 * @param name
	 *            le nom du widget
	 */
	void setName(String name);

	/**
	 * Récupère le nom du widget.
	 * 
	 * @return nom du widget
	 */
	String getName();

	/**
	 * Met en place le type du widget. (string, date, boolean ...)
	 * 
	 * @param name
	 *            le nom du widget
	 */
	void setType(String type);

	/**
	 * Récupère le type du widget.
	 * 
	 * @return type du widget
	 */
	String getType();

	/**
	 * Retourne le nom du widget
	 * 
	 * @return
	 */
	String getWidgetName();

	/**
	 * Retourne le libellé du widget
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Retourne les propriétés supplémentaires du widget (en dehors du nom, du label et du type)
	 * 
	 * @return une map contenant les noms des propriétés comme clé, et leur valeur comme valeurs.
	 */
	Map<String, String> getExtraProperties();

	/**
	 * Met en place les propriétés supplémentaires du widget (en dehors du nom, du label et du type)
	 * 
	 * @param extraProperties
	 *            Les propriétés supplémentaires
	 */
	void setExtraProperties(Map<String, String> extraProperties);

	/**
	 * Si il existe, retourne le nom du vocabulaire sur lequel est basé le widget, sinon la chaîne vide.
	 * 
	 * @return Le nom du vocabulaire
	 */
	String getDirectoryName();

	/**
	 * Si la propriété HAS_TO_CONVERT_LABEL existe dans les propriétés du widget, retourne sa valeur. Sinon, retourne
	 * faux.
	 * 
	 * @return Un booléen indiquant si le vocabulaire possède des labels d'internationalisation.
	 */
	Boolean getHasToConvertLabel();

	/**
	 * Retourne un ensemble composé de la catégorie du widget et du nom Ceci afin de pouvoir avoir plusieurs widgets
	 * différents avec des catégories différentes, mais de même nom
	 * 
	 * @return
	 */
	String getNameWithCategory();

	/**
	 * Retourne un ensemble composé de la catégorie du widget et du nom de widget Ceci afin de pouvoir avoir plusieurs
	 * widgets différents avec des catégories différentes, mais de même nom
	 * 
	 * @return
	 */
	String getWidgetNameWithCategory();
}
