package fr.dila.st.core.requeteur;

import fr.dila.st.api.constant.STRequeteurExpertConstants;
import fr.dila.st.api.requeteur.RequeteurAddInfoField;
import fr.dila.st.api.requeteur.RequeteurContribution;
import fr.dila.st.api.requeteur.RequeteurContributionBuilder;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import fr.dila.st.api.requeteur.SelectedDocument;
import fr.dila.st.core.util.CollectionHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.RuntimeContext;

public class RequeteurContributionImpl implements RequeteurContribution {
    private List<SelectedDocument> selectedDocuments;

    private RequeteurContributionBuilder builder;

    protected Map<String, RequeteurWidgetDescription> widgetDescriptionRegistry;

    protected Map<String, RequeteurAddInfoField> addInfoFieldsRegistry;

    private static final Log LOGGER = LogFactory.getLog(RequeteurContributionImpl.class);

    public RequeteurContributionImpl() {
        selectedDocuments = new ArrayList<>();
        widgetDescriptionRegistry = new HashMap<>();
        addInfoFieldsRegistry = new HashMap<>();
        builder = null;
    }

    public List<SelectedDocument> getSelectedDocuments() {
        return selectedDocuments;
    }

    public void setSelectedDocuments(List<SelectedDocument> selectedDocuments) {
        this.selectedDocuments = selectedDocuments;
    }

    @Override
    public RequeteurContributionBuilder getBuilder() {
        return builder;
    }

    @Override
    public void setBuilder(RequeteurContributionBuilder builder) {
        this.builder = builder;
    }

    @Override
    public Map<String, RequeteurWidgetDescription> getWidgetDescriptionRegistry() {
        return widgetDescriptionRegistry;
    }

    public void setWidgetDescriptionRegistry(Map<String, RequeteurWidgetDescription> widgetDescriptionRegistry) {
        this.widgetDescriptionRegistry = widgetDescriptionRegistry;
    }

    public Map<String, RequeteurAddInfoField> getAddInfoFieldsRegistry() {
        return addInfoFieldsRegistry;
    }

    public void setAddInfoFieldsRegistry(Map<String, RequeteurAddInfoField> addInfoFieldsRegistry) {
        this.addInfoFieldsRegistry = addInfoFieldsRegistry;
    }

    @Override
    public void removeSelectedDocument(SelectedDocument selectedDocument) {
        selectedDocuments.remove(selectedDocument);
    }

    @Override
    public void addSelectedDocument(SelectedDocument selectedDocument) {
        selectedDocuments.add(selectedDocument);
    }

    @Override
    public void addWidgetDescription(RequeteurWidgetDescription widgetDescription) {
        String name = widgetDescription.getNameWithCategory();
        LOGGER.debug("Enregistrement du widget " + name + " cat : " + widgetDescription.getCategory() + "|");
        if (widgetDescriptionRegistry.containsKey(name)) {
            widgetDescriptionRegistry.remove(name);
        }
        widgetDescriptionRegistry.put(name, widgetDescription);
    }

    @Override
    public void addAddInfoField(RequeteurAddInfoField addInfoField) {
        String name = addInfoField.getName();
        if (addInfoFieldsRegistry.containsKey(name)) {
            addInfoFieldsRegistry.remove(name);
        }
        addInfoFieldsRegistry.put(name, addInfoField);
    }

    @Override
    public void removeAddInfoField(RequeteurAddInfoField addInfoField) {
        String name = addInfoField.getName();
        addInfoFieldsRegistry.remove(name);
    }

    @Override
    public void removeWidgetDescription(RequeteurWidgetDescription widgetDescription) {
        String name = widgetDescription.getNameWithCategory();
        widgetDescriptionRegistry.remove(name);
    }

    public URL createContribution() throws Exception {
        if (builder == null) {
            LOGGER.error("Builder null, rien a déployer");
            return null;
        }
        Collection<RequeteurWidgetDescription> descriptions = getSortedWidgetDescriptionList(
            widgetDescriptionRegistry.values()
        );
        return builder.createRequeteurContribution(descriptions);
    }

    /**
     * Renvoie une liste de description de widget triée
     *
     * @param initialCollection
     *            la collection de widget initiale
     * @return une liste de widget avec les informations supplémentaires concernant le type, la catégory, etc ...
     */
    protected List<RequeteurWidgetDescription> getSortedWidgetDescriptionList(
        Collection<RequeteurWidgetDescription> initialCollection
    ) {
        return CollectionHelper.asSortedList(initialCollection, new CategoryWidgetDescriptionComparator());
    }

    /**
     * Ajoute à chaque widget les informations supplémentaires possible et met à jour le registre de description
     *
     * @return
     */
    public void refreshExtraInformationForWidgetDescriptionRegistry() {
        for (RequeteurWidgetDescription description : widgetDescriptionRegistry.values()) {
            if (addInfoFieldsRegistry != null && addInfoFieldsRegistry.containsKey(description.getName())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("AddInfoField trouvé pour " + description.getName());
                }
                RequeteurAddInfoField addInfoField = addInfoFieldsRegistry.get(description.getName());
                if (addInfoField.getNewType() != null) {
                    description.setType(addInfoField.getNewType());
                }
                if (addInfoField.getNewCategory() != null) {
                    description.setCategory(addInfoField.getNewCategory());
                }
                description.setExtraProperties(addInfoField.getProperties());
            }
        }
    }

    public Map<String, RequeteurWidgetDescription> getWigetDescriptionRegistry() {
        return widgetDescriptionRegistry;
    }

    @Override
    public List<String> getCategories() {
        Set<String> categories = new HashSet<>();
        categories.add(STRequeteurExpertConstants.REQUETEUR_EXPERT_ALL_CATEGORY);
        widgetDescriptionRegistry.values().forEach(widget -> categories.add(widget.getCategory()));
        return CollectionHelper.asSortedList(categories);
    }

    public void buildAndDeploy(RuntimeContext context) {
        try {
            LOGGER.info("Génération des widgets experts et déploiement");
            URL url = createContribution();
            context.deploy(url);
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
    }

    @Override
    public RequeteurWidgetDescription getWidgetDescriptionBySearchField(String searchField) {
        if (searchField == null) {
            return null;
        }
        for (RequeteurWidgetDescription widget : widgetDescriptionRegistry.values()) {
            if (searchField.equals(widget.getName())) {
                return widget;
            }
        }
        return null;
    }
}
