package fr.dila.st.api.requeteur;

import java.util.List;
import java.util.Map;
import org.nuxeo.runtime.model.RuntimeContext;

/**
 * Interface pour la contribution de widget requeteur.
 *
 * @author jgomez
 *
 */
public interface RequeteurContribution {
    void removeSelectedDocument(SelectedDocument selectedDocument);

    void addSelectedDocument(SelectedDocument selectedDocument);

    void addWidgetDescription(RequeteurWidgetDescription widgetDescription);

    void addAddInfoField(RequeteurAddInfoField addInfoField);

    void removeAddInfoField(RequeteurAddInfoField addInfoField);

    void removeWidgetDescription(RequeteurWidgetDescription widgetDescription);

    RequeteurContributionBuilder getBuilder();

    void setBuilder(RequeteurContributionBuilder builder);

    List<String> getCategories();

    Map<String, RequeteurWidgetDescription> getWidgetDescriptionRegistry();

    void refreshExtraInformationForWidgetDescriptionRegistry();

    /**
     * Construit la conribution temporaire et la déploie
     *
     * @param runtimeContext
     *            le context du serveur nuxeo
     */
    void buildAndDeploy(RuntimeContext runtimeContext);

    /**
     * Retourne le widget à partir du search field
     *
     * @param searchField
     *            le champ de recherche
     * @return
     */
    RequeteurWidgetDescription getWidgetDescriptionBySearchField(String searchField);
}
