package fr.dila.st.core.descriptor;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import fr.dila.st.api.constant.STRequeteurExpertConstants;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;

/**
 * 
 * Permet de récupérer les élements qui forment la définition d'un widget du requêteur. Gràce à ces informations, on a
 * suffisament d'information pour créer une widget dans le requêteur.
 * 
 * @author jgomez
 * 
 */
@XObject("widgetDescription")
public class WidgetDescriptionDescriptor implements RequeteurWidgetDescription {

	@XNode("@category")
	private String				category;

	@XNode("@name")
	private String				name;

	@XNode("@type")
	private String				type;

	private Map<String, String>	extraProperties;

	/**
	 * Default constructor
	 */
	public WidgetDescriptionDescriptor() {
		// do nothing
	}

	@Override
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String getCategory() {
		return category;
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
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getWidgetName() {
		return "requeteur_" + name.replaceAll("\\.", "_").replaceAll(":", "_");
	}

	@Override
	public String getWidgetNameWithCategory() {
		return "requeteur_" + getNameWithCategory().replaceAll("\\.", "_").replaceAll(":", "_");
	}

	@Override
	public String getNameWithCategory() {
		return category + "." + name;
	}

	@Override
	public String getLabel() {
		return "label.requeteur." + name.replaceAll(":", ".");
	}

	@Override
	public Map<String, String> getExtraProperties() {
		return this.extraProperties;
	}

	@Override
	public void setExtraProperties(Map<String, String> extraProperties) {
		this.extraProperties = extraProperties;
	}

	@Override
	public String getDirectoryName() {
		if (this.extraProperties.containsKey(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_DIRECTORY_NAME)) {
			return this.getExtraProperties().get(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_DIRECTORY_NAME);

		} else {
			return StringUtils.EMPTY;
		}
	}

	@Override
	public Boolean getHasToConvertLabel() {
		if (this.extraProperties
				.containsKey(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_DIRECTORY_HAS_TO_CONVERT_LABEL)) {
			return this.getExtraProperties()
					.get(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_DIRECTORY_HAS_TO_CONVERT_LABEL).equals("true");

		} else {
			return Boolean.FALSE;
		}
	}

	public String toString() {
		return String.format("\t<widgetDescription category=\"%s\" name=\"%s\" type=\"%s\"/>", getCategory(),
				getName(), getType());
	}

}
