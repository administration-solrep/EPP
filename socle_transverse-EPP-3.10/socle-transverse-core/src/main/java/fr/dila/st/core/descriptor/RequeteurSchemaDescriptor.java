package fr.dila.st.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * La classe utilisée pour contenir les informations du schéma.
 * 
 * @author jgomez
 * 
 */
@XObject
public class RequeteurSchemaDescriptor {

	@XNode("@name")
	private String	name;

	@XNode("@defaultCategory")
	private String	defaultCategory;

	/**
	 * Default constructor
	 */
	public RequeteurSchemaDescriptor() {
		// do nothing
	}

	public String getDefaultCategory() {
		return defaultCategory;
	}

	public String getName() {
		return name;
	}

}
