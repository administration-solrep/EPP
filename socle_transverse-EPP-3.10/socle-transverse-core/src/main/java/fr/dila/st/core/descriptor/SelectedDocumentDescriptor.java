package fr.dila.st.core.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.SchemaManagerImpl;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import fr.dila.st.api.requeteur.SelectedDocument;
import fr.dila.st.core.factory.STLogFactory;

/**
 * 
 * Un document sélectionné pour avoir ses champs utilisés par le requêteur. On se sert de ce descripteur pour extraire
 * des informations sur les schémas que l'on désire requêter, les champs que l'on désire exclure du requêteur.
 * 
 * @author jgomez
 * 
 */
@XObject("selectedDocument")
public class SelectedDocumentDescriptor implements SelectedDocument {

	private static final String						ALL_CATEGORIES	= "Tous";

	private static final STLogger					LOGGER			= STLogFactory
																			.getLog(SelectedDocumentDescriptor.class);

	@XNode("@name")
	private String									name;

	@XNode("@key")
	private String									key;

	@XNodeMap(value = "schemas/schema", key = "@name", type = HashMap.class, componentType = RequeteurSchemaDescriptor.class)
	private Map<String, RequeteurSchemaDescriptor>	schemas;

	@XNodeList(value = "excludedField@name", type = String[].class, componentType = String.class)
	private String[]								excludedFields;

	@XNodeList(value = "addedField@name", type = String[].class, componentType = String.class)
	private String[]								addedFields;

	/**
	 * Default constructor
	 */
	public SelectedDocumentDescriptor() {
		// do nothing
	}

	@Override
	public void setExcludedFields(String[] excludedFields) {
		this.excludedFields = excludedFields.clone();
	}

	@Override
	public Set<String> getExcludedFields() {
		return new HashSet<String>(Arrays.asList(this.excludedFields));
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	protected String getFullFieldName(Field field) {
		return getKey() + field.getName().toString();
	}

	@Override
	public Collection<? extends RequeteurWidgetDescription> createWidgetDescriptions(
			final SchemaManagerImpl schemaManager) {
		List<RequeteurWidgetDescription> results = new ArrayList<RequeteurWidgetDescription>();
		DocumentType docType = schemaManager.getDocumentType(this.getName());
		if (docType == null) {
			LOGGER.debug(STLogEnumImpl.FAIL_GET_DOCTYPE_TEC, "No docType with this name " + this.getName());
			LOGGER.error(STLogEnumImpl.FAIL_GET_DOCTYPE_TEC);
			return null;
		}
		// Ajout des champs des schémas
		for (String schemaName : this.getSchemas()) {
			Schema schema = docType.getSchema(schemaName);
			if (schema == null) {
				LOGGER.debug(STLogEnumImpl.FAIL_GET_SCHEMA_TEC, "No schema " + schemaName + " on " + this.getName());
				LOGGER.error(STLogEnumImpl.FAIL_GET_SCHEMA_TEC);
				return null;
			}
			for (Field field : schema.getFields()) {
				String fullFieldName = getFullFieldName(field);
				if (!this.getExcludedFields().contains(fullFieldName)) {
					RequeteurWidgetDescription description = new WidgetDescriptionDescriptor();
					description.setCategory(getDefaultCategory(schemaName));
					description.setName(getFullFieldName(field));
					description.setType(getType(field));
					results.add(description);
				}
			}

		}
		// Ajout des champs supplémentaires
		for (String extraFieldName : this.getAddedFields()) {
			RequeteurWidgetDescription description = new WidgetDescriptionDescriptor();
			description.setName(extraFieldName);
			description.setType("fulltext");
			description.setCategory("dossier");
			results.add(description);
		}

		return results;
	}

	/**
	 * On renvoie un type qui permet de trouver le template correspondant au widget. Habituellement, un type nuxeo, mais
	 * on peut trouver des types particuliers (manyDirectory, orga) si on a une information addTypeInfo
	 * 
	 * @param field
	 * @return
	 */
	protected String getType(Field field) {
		return field.getType().getName();
	}

	protected String getDefaultCategory(String schemaName) {
		if (this.schemas.containsKey(schemaName)) {
			return this.schemas.get(schemaName).getDefaultCategory();
		} else {
			return ALL_CATEGORIES;
		}
	}

	@Override
	public String[] getSchemas() {
		return this.schemas.keySet().toArray(new String[this.schemas.keySet().size()]);
	}

	@Override
	public void setAddedFields(String[] addedFields) {
		this.addedFields = addedFields.clone();
	}

	@Override
	public String[] getAddedFields() {
		return addedFields.clone();
	}

}
