package fr.dila.solonepp.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.forms.layout.api.FieldDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.LayoutRowDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.impl.FieldDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.LayoutDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.LayoutRowDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.service.WebLayoutManagerImpl;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import com.google.common.collect.Sets;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLayoutConstant;
import fr.dila.solonepp.api.constant.SolonEppMetaConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.api.service.MetadonneeWebService;
import fr.dila.st.core.service.STComponentLocator;

/**
 * Implémentation du service permettant de contribuer les composants Web correspondant
 * aux métadonnées des événements.
 * 
 * @author jtremeaux
 */
public class MetadonneeWebServiceImpl extends DefaultComponent implements MetadonneeWebService {

    /**
     * Préfixe des widgets des recherches de métadonnées.
     */
    public static final String RECHERCHE_DOCUMENTAIRE_CRITERE_METADONNEE_WIDGET_PREFIX = "recherche_documentaire_critere_metadonnee_";
    
    /**
     * Serial UID.
     */
	private static final long serialVersionUID = 8266232282826240132L;
	
	private static final String SELECT_ONE_DIR_WIDGET_TYPE = "selectOneDirectory";
	
	private static final String TABLE_REF_WIDGET_TYPE = "table_ref";
	
	private static final String DIRECTORY_NAME_KEY = "directoryName";
	
	private static final String TABLE_REFERENCE_KEY = "tableReference";
	
	private static final String EMETTEUR_KEY = "emetteur";

    @Override
	public void activate(ComponentContext context) throws Exception {
        contributeMetadonneeLayout();
	}

   /**
    * Contribue le layout de recherche dynamique sur les métadonnées et les widgets
    * à partir du service des métadonnées.
    * 
    * @throws ClientException
    */
    public void contributeMetadonneeLayout() throws ClientException {
        // Détermine la catégorie des types d'événements
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final Map<String, String> evenementTypeCategorieMap = new HashMap<String, String>();
        for (EvenementTypeDescriptor evenementTypeDescriptor : evenementTypeService.findEvenementType()) {
            evenementTypeCategorieMap.put(evenementTypeDescriptor.getName(), evenementTypeDescriptor.getProcedure());
        }
        
        // Classe les catégories d'événement par widget
        final MetaDonneesService metaDonneeService = SolonEppServiceLocator.getMetaDonneesService();
        List<MetaDonneesDescriptor> metadonneDescriptorList = metaDonneeService.getAll();
        final Map<String, Set<String>> widgetCategorieMap = new HashMap<String, Set<String>>();
        for (MetaDonneesDescriptor metadonneeDescriptor : metadonneDescriptorList) {
            String evenementType = metadonneeDescriptor.getName();
            VersionMetaDonneesDescriptor versionMetadonneeDescriptor = metadonneeDescriptor.getVersion();
            for (Entry<String, PropertyDescriptor> entry : versionMetadonneeDescriptor.getProperty().entrySet()) {
                final String key = entry.getKey();
                Set<String> categorieSet = widgetCategorieMap.get(key);
                if (categorieSet == null) {
                    categorieSet = new HashSet<String>();
                    widgetCategorieMap.put(key, categorieSet);
                }
                categorieSet.add(evenementTypeCategorieMap.get(evenementType));
            }
        }
        
        WebLayoutManagerImpl webLayoutManagerImpl = STComponentLocator.getWebLayoutManagerImpl();
        Map<String, String> templates = new HashMap<String, String>();
        templates.put("any", "/layouts/layout_default_template.xhtml");
        List<LayoutRowDefinition> rows = new ArrayList<LayoutRowDefinition>();

        Map<String, WidgetDefinition> widgetDefinitionMap = new LinkedHashMap<String, WidgetDefinition>();
        for (MetaDonneesDescriptor metadonneeDescriptor : metadonneDescriptorList) {
            VersionMetaDonneesDescriptor versionMetadonneeDescriptor = metadonneeDescriptor.getVersion();
            for (Entry<String, PropertyDescriptor> entry : versionMetadonneeDescriptor.getProperty().entrySet()) {
                addWidgetDefinition(widgetDefinitionMap, entry.getValue(), widgetCategorieMap);
            }
        }
        
        for (String widgetName : widgetDefinitionMap.keySet()) {
            LayoutRowDefinition row = new LayoutRowDefinitionImpl(null, widgetName);
            rows.add(row);
        }
        
        List<WidgetDefinition> widgetDefinitionList = new ArrayList<WidgetDefinition>(widgetDefinitionMap.values());
        LayoutDefinitionImpl layout = new LayoutDefinitionImpl(SolonEppLayoutConstant.RECHERCHE_DOCUMENTAIRE_CRITERE_METADONNEE_LAYOUT, null, templates, rows, widgetDefinitionList);
        webLayoutManagerImpl.registerContribution(layout, WebLayoutManagerImpl.LAYOUTS_EP_NAME, null);
    }
    
    /**
     * Ajoute la description du (des) widget(s) à partir de la définition d'une métadonnée.
     * 
     * @param widgetDefinitionMap Table contenant tous les widgets par procédure
     * @param propertyDescriptor Descripteur de la métadonnée
     * @param widgetCategorieMap Ensemble des catégories classées par identifiant de widget
     * @throws ClientException
     */
    protected void addWidgetDefinition(Map<String, WidgetDefinition> widgetDefinitionMap, PropertyDescriptor propertyDescriptor, Map<String, Set<String>> widgetCategorieMap) throws ClientException {
        final String propertyName = propertyDescriptor.getName();
        final String propertyType = propertyDescriptor.getType();

        // Ne construit pas de widget pour certaines propriétés
        Set<String> ignoredMetadata = Sets.newHashSet(SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY,
           //Niveau numero a prendre avec widget du niveau code
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY);
        if (ignoredMetadata.contains(propertyName)) {
            return;
        }
        
        // Si le widget a déja été ajouté pour cette procédure, rien à faire
        String widgetName = RECHERCHE_DOCUMENTAIRE_CRITERE_METADONNEE_WIDGET_PREFIX + propertyName;
        if (widgetDefinitionMap.containsKey(widgetName)) {
            return;
        }
        
        String type = null;
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("any", "label.epp.version." + propertyName);
        Map<String, String> helpLabels = new HashMap<String, String>();
        Map<String, String> modes = new HashMap<String, String>();
        Map<String, Map<String, Serializable>> widgetModeProperties = new HashMap<String, Map<String, Serializable>>();
        List<String> conditionList = new ArrayList<String>();
        for (String categorie : widgetCategorieMap.get(propertyName)) {
            String condition = "rechercheDocumentaireActions.categorieEvenementId eq '" + categorie + "'";
            conditionList.add(condition);
        }
        modes.put("any", "#{" + StringUtils.join(conditionList, " or ") + " ? 'edit' : 'hidden'}");
        List<FieldDefinition> fieldDefinitionList = new ArrayList<FieldDefinition>();
        FieldDefinition fieldDefinition = new FieldDefinitionImpl(null, "metadonneeCriteria['" + propertyDescriptor.getName() + "']");
        fieldDefinitionList.add(fieldDefinition);
        
        if (SelectWidgetMetadonneesEnum.containsProperty(propertyType)) {
        	SelectWidgetMetadonneesEnum value = SelectWidgetMetadonneesEnum.getEnumFromProperty(propertyType);
			if (value != null) {
				Map<String, Serializable> propertiesMap = value.getProperties();
				widgetModeProperties.put("any", propertiesMap);
				WidgetDefinition widgetDefinition = new WidgetDefinitionImpl(widgetName, value.getWidgetType(), labels,
						helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
				widgetDefinitionMap.put(widgetName, widgetDefinition);
			}
        } else if (SolonEppMetaConstant.META_TYPE_STRING.equals(propertyType)) {
            type = "text";
            WidgetDefinition widgetDefinition = new WidgetDefinitionImpl(widgetName, type, labels, helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
            widgetDefinitionMap.put(widgetName, widgetDefinition);
        } else if (SolonEppMetaConstant.META_TYPE_NIVEAU_LECTURE.equals(propertyType)) {
          //utiliser le type niveau_lecture_widget pour prendre le niveau numero aussi
            type = "niveau_lecture_widget";
            fieldDefinitionList = new ArrayList<FieldDefinition>();
            fieldDefinition = new FieldDefinitionImpl(null, "metadonneeCriteria['"+SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY+"']");
            fieldDefinitionList.add(fieldDefinition);
            fieldDefinition = new FieldDefinitionImpl(null, "metadonneeCriteria['"+SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY+"']");
            fieldDefinitionList.add(fieldDefinition);
            Map<String, Serializable> propertiesMap = new HashMap<String, Serializable>();
            widgetModeProperties.put("any", propertiesMap);
            propertiesMap.put("directoryName", SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY);
            WidgetDefinition widgetDefinition = new WidgetDefinitionImpl(widgetName, type, labels, helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
            widgetDefinitionMap.put(widgetName, widgetDefinition);
        } else if (SolonEppMetaConstant.META_TYPE_INTEGER.equals(propertyType)) {
            type = "text";
            WidgetDefinition widgetDefinition = new WidgetDefinitionImpl(widgetName, type, labels, helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
            widgetDefinitionMap.put(widgetName, widgetDefinition);
        } else if (SolonEppMetaConstant.META_TYPE_DATE.equals(propertyType)) {
            type = "datetime";
            Map<String, Serializable> propertiesMap = new HashMap<String, Serializable>();
            widgetModeProperties.put("any", propertiesMap);
            propertiesMap.put("format", "dd/MM/yyyy");
            WidgetDefinition widgetDefinition = new WidgetDefinitionImpl(widgetName, type, labels, helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
            widgetDefinitionMap.put(widgetName, widgetDefinition);

            widgetName = RECHERCHE_DOCUMENTAIRE_CRITERE_METADONNEE_WIDGET_PREFIX + propertyName + "_fin";
            labels = new HashMap<String, String>();
            labels.put("any", "epp.rechercheMessage.criteria.evenementPeriode");
            fieldDefinitionList = new ArrayList<FieldDefinition>();
            fieldDefinition = new FieldDefinitionImpl(null, "metadonneeCriteria['" + propertyDescriptor.getName() + "_fin']");
            fieldDefinitionList.add(fieldDefinition);
            widgetDefinition = new WidgetDefinitionImpl(widgetName, type, labels, helpLabels, true, modes, fieldDefinitionList, null, widgetModeProperties, null);
            widgetDefinitionMap.put(widgetName, widgetDefinition);
        } else {
            throw new ClientException("Type de métadonnée inconnu: " + propertyType);
        }
    }
    
    /**
     * Pour générer les champs de métadonnées. 
     * L'enumeration prend le type de la métadonnée, le widget type, et l'ensemble des propriétés nécessaires à la génération
     *
     */
    private enum SelectWidgetMetadonneesEnum {
    	/* ******************* META TYPE SELECT ONE DIRECTORY **************** */
    	META_TYPE_BOOLEAN(SolonEppMetaConstant.META_TYPE_BOOLEAN, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.BOOLEAN_VOCABULARY),
    	META_TYPE_TYPE_ACTE(SolonEppMetaConstant.META_TYPE_TYPE_ACTE, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.TYPE_ACTE_VOCABULARY),
    	META_TYPE_TYPE_LOI(SolonEppMetaConstant.META_TYPE_TYPE_LOI, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY),
    	META_TYPE_RESULTAT_CMP(SolonEppMetaConstant.META_TYPE_RESULTAT_CMP, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.RESULTAT_CMP_VOCABULARY),
    	META_TYPE_ATTRIBUTION_COMMISSION(SolonEppMetaConstant.META_TYPE_ATTRIBUTION_COMMISSION, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY),
    	META_TYPE_SORT_ADOPTION(SolonEppMetaConstant.META_TYPE_SORT_ADOPTION, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY),
    	META_TYPE_SENS_AVIS(SolonEppMetaConstant.META_TYPE_SENS_AVIS, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY),
    	META_TYPE_RAPPORT_PARLEMENT(SolonEppMetaConstant.META_TYPE_RAPPORT_PARLEMENT, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.RAPPORT_PARLEMENT_VOCABULARY),
    	META_TYPE_NATURE_RAPPORT(SolonEppMetaConstant.META_TYPE_NATURE_RAPPORT, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY),
    	META_TYPE_NATURE_LOI(SolonEppMetaConstant.META_TYPE_NATURE_LOI, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY),
    	META_TYPE_MOTIF_IRRECEVABILITE(SolonEppMetaConstant.META_TYPE_MOTIF_IRRECEVABILITE, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY),
    	META_TYPE_DECISION_PROC_ACC(SolonEppMetaConstant.META_TYPE_DECISION_PROC_ACC, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.DECISION_PROC_ACC),
		META_TYPE_RUBRIQUE(SolonEppMetaConstant.META_TYPE_RUBRIQUE, SELECT_ONE_DIR_WIDGET_TYPE, DIRECTORY_NAME_KEY, SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY),
    	
    	/* ****************** META TYPE TABLE REF ******************************* */
    	META_TYPE_PERIODE(SolonEppMetaConstant.META_TYPE_PERIODE, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.PERIODE_DOC_TYPE),
    	META_TYPE_CIRCONSCRIPTION(SolonEppMetaConstant.META_TYPE_CIRCONSCRIPTION, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE),
    	META_TYPE_GOUVERNEMENT(SolonEppMetaConstant.META_TYPE_GOUVERNEMENT, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.GOUVERNEMENT_DOC_TYPE),
    	META_TYPE_IDENTITE(SolonEppMetaConstant.META_TYPE_IDENTITE, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.IDENTITE_DOC_TYPE, EMETTEUR_KEY, "ALL"),
    	META_TYPE_MANDAT(SolonEppMetaConstant.META_TYPE_MANDAT, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.IDENTITE_DOC_TYPE, EMETTEUR_KEY, "ALL"),
    	META_TYPE_MEMBRE_GROUPE(SolonEppMetaConstant.META_TYPE_MEMBRE_GROUPE, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE),
    	META_TYPE_MINISTERE(SolonEppMetaConstant.META_TYPE_MINISTERE, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.MINISTERE_DOC_TYPE),
    	META_TYPE_ORGANISME(SolonEppMetaConstant.META_TYPE_ORGANISME, TABLE_REF_WIDGET_TYPE, TABLE_REFERENCE_KEY, SolonEppConstant.ORGANISME_DOC_TYPE);
    	
    	private String widgetType;
    	private String propertyType;
    	private Map<String, Serializable> properties;

    	SelectWidgetMetadonneesEnum(String propertyType, String widgetType, String...properties) {
        	this.propertyType = propertyType;
        	this.widgetType = widgetType;
        	int cptProperties = 0;
        	this.properties = new HashMap<String, Serializable>();
        	while (cptProperties < properties.length) {
        		this.properties.put(properties[cptProperties++], properties[cptProperties++]);
        	}
        }
    	
    	public static boolean containsProperty(String property) {
    		for (SelectWidgetMetadonneesEnum value : values()) {
    			if (value.getPropertyType().equals(property)) {
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	public static SelectWidgetMetadonneesEnum getEnumFromProperty(String property) {
    		for (SelectWidgetMetadonneesEnum value : values()) {
    			if (value.getPropertyType().equals(property)) {
    				return value;
    			}
    		}
    		return null;
    	}
    	
    	public String getPropertyType() {
    		return propertyType;
    	}
    	
    	public Map<String, Serializable> getProperties() {
    		return new HashMap<String, Serializable>(properties);
    	}
    	
    	public String getWidgetType() {
    		return widgetType;
    	}
    };
    
}
