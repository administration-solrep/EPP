package fr.dila.st.api.requeteur;

import java.net.URL;
import java.util.Collection;

/**
 * Interface pour les constructeurs de contributions requêteurs experts.
 *
 * @author jgomez
 *
 */
public interface RequeteurContributionBuilder {
    /**
     * Génére le fichier temporaire contenant les widgets et le layout pour un requêteur expert
     *
     * @param widgetDescriptions
     * @return
     * @throws Exception
     */
    URL createRequeteurContribution(Collection<RequeteurWidgetDescription> widgetDescriptions) throws Exception;

    /**
     * Le nom de fichier template du layout
     *
     * @param templateName
     */
    void setFileName(String templateName);

    /**
     * Le nom de contribution
     *
     * @param name
     */
    void setContribName(String name);

    /**
     * Le nom de composant de la contribution générée.
     *
     * @param componentName
     */
    void setComponentName(String componentName);

    /**
     * Le nom du layout généré
     *
     * @param layoutName
     */
    void setLayoutName(String layoutName);

    /**
     * Si vrai, affiche la sélection des catégories dans le requêteur
     *
     * @param showcategories
     */
    void setShowCategories(Boolean showcategories);
}
