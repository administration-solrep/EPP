package fr.dila.st.core.service;

import fr.dila.st.api.requeteur.RequeteurAddInfoField;
import fr.dila.st.api.requeteur.RequeteurContribution;
import fr.dila.st.api.requeteur.RequeteurContributionBuilder;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import fr.dila.st.api.requeteur.SelectedDocument;
import fr.dila.st.api.service.STRequeteurWidgetGeneratorService;
import fr.dila.st.core.descriptor.ContributionBuilderDescriptor;
import fr.dila.st.core.requeteur.RequeteurContributionImpl;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class STRequeteurWidgetGeneratorServiceImpl
    extends DefaultComponent
    implements STRequeteurWidgetGeneratorService {
    private static final String CONTRIBUTION_BUILDER_EP = "contributionbuilders";

    private static final String SELECTED_DOCUMENTS_EP = "selecteddocuments";

    private static final String WIDGET_DESCRIPTIONS_EP = "widgetdescriptions";

    private static final String ADD_INFO_FIELDS_EP = "addinfofields";

    private static final Log LOGGER = LogFactory.getLog(STRequeteurWidgetGeneratorServiceImpl.class);

    private Map<String, RequeteurContribution> mapContribution;

    /**
     * Default constructor
     */
    public STRequeteurWidgetGeneratorServiceImpl() {
        super();
    }

    @Override
    public void activate(ComponentContext context) {
        mapContribution = new HashMap<String, RequeteurContribution>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        mapContribution = null;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        String contributorName = contributor.getName().getName();
        RequeteurContribution reqContribution = getOrCreateRequeteurContribution(contributorName);
        if (CONTRIBUTION_BUILDER_EP.equals(extensionPoint)) {
            ContributionBuilderDescriptor desc = (ContributionBuilderDescriptor) contribution;
            if (desc == null) {
                throw new IllegalArgumentException("Pas de contribution sur le point d'extension contributionbuilders");
            }
            try {
                RequeteurContributionBuilder builder = desc.getKlass().newInstance();
                builder.setFileName(desc.getTemplateName());
                builder.setContribName(desc.getName());
                builder.setComponentName(desc.getComponentName());
                builder.setLayoutName(desc.getLayoutName());
                builder.setShowCategories(desc.getShowcategories());
                reqContribution.setBuilder(builder);
            } catch (InstantiationException e) {
                LOGGER.error("Builder de classe " + desc.getKlass() + "non trouvé");
                throw new IllegalArgumentException("Builder de classe " + desc.getKlass() + " non trouvé");
            } catch (IllegalAccessException e) {
                LOGGER.error("Builder de classe " + desc.getKlass() + "non trouvé");
                throw new IllegalArgumentException("Problème d'accès au builder de classe " + desc.getKlass());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Utilisation de l'objet builder de classe = " + desc.getKlass());
            }
        }

        if (SELECTED_DOCUMENTS_EP.equals(extensionPoint)) {
            reqContribution.addSelectedDocument((SelectedDocument) contribution);
        }
        if (WIDGET_DESCRIPTIONS_EP.equals(extensionPoint)) {
            RequeteurWidgetDescription widgetDescription = (RequeteurWidgetDescription) contribution;
            reqContribution.addWidgetDescription(widgetDescription);
        }
        if (ADD_INFO_FIELDS_EP.equals(extensionPoint)) {
            RequeteurAddInfoField addInfoField = (RequeteurAddInfoField) contribution;
            reqContribution.addAddInfoField(addInfoField);
        }
    }

    private RequeteurContribution getOrCreateRequeteurContribution(String contributorName) {
        if (!mapContribution.containsKey(contributorName)) {
            mapContribution.put(contributorName, new RequeteurContributionImpl());
        }
        return mapContribution.get(contributorName);
    }

    @Override
    public void applicationStarted(ComponentContext context) {
        for (RequeteurContribution requContribution : mapContribution.values()) {
            requContribution.refreshExtraInformationForWidgetDescriptionRegistry();
            requContribution.buildAndDeploy(context.getRuntimeContext());
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        String contributorName = contributor.getName().getName();
        RequeteurContribution reqContribution = getOrCreateRequeteurContribution(contributorName);
        if (SELECTED_DOCUMENTS_EP.equals(extensionPoint)) {
            SelectedDocument selectedDocument = (SelectedDocument) contribution;
            reqContribution.removeSelectedDocument(selectedDocument);
        }
        if (WIDGET_DESCRIPTIONS_EP.equals(extensionPoint)) {
            RequeteurWidgetDescription widgetTemplate = (RequeteurWidgetDescription) contribution;
            reqContribution.removeWidgetDescription(widgetTemplate);
        }
        if (ADD_INFO_FIELDS_EP.equals(extensionPoint)) {
            RequeteurAddInfoField addInfoField = (RequeteurAddInfoField) contribution;
            reqContribution.removeAddInfoField(addInfoField);
        }
    }

    protected static SchemaManager getTypeManager() throws Exception {
        return ServiceUtil.getRequiredService(SchemaManager.class);
    }

    @Override
    public RequeteurContribution getRequeteurContribution(String contributorName) {
        return mapContribution.get(contributorName);
    }
}
