package fr.dila.st.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur de la configuration de l'application.
 * 
 * @author jtremeaux
 */
@XObject("parameter")
public class ConfigParameterDescriptor {

	@XNode("name")
	private String	name;

	@XNode("value")
	private String	value;

	/**
	 * Default constructor
	 */
	public ConfigParameterDescriptor() {
		// do nothing
	}

	/**
	 * Getter de name.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter de name.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter de value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter de value.
	 * 
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
