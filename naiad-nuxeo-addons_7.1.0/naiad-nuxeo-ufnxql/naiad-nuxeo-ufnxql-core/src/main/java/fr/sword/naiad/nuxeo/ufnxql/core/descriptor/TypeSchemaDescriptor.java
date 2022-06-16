package fr.sword.naiad.nuxeo.ufnxql.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * &lt;typeSchema type="doc_type" schema="schema_name"/&gt;
 * @author SPL
 *
 */
@XObject("typeSchema")
public final class TypeSchemaDescriptor {

	@XNode("@type")
	private String type;
	
	@XNode("@schema")
	private String schema;
	
	/**
	 * default constructor
	 */
	public TypeSchemaDescriptor() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	@Override
	public String toString(){
		return "TypeSchemaDescriptor[" + type + "|" + schema + "]";
	}
	
}

