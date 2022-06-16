package fr.sword.naiad.nuxeo.ufnxql.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;

/**
 * Descriptor qui permet de déclarer de nouvelle fonction supporter par le language UFNXQL en procurant une classe implémentant
 * l'interface UfnxqlFunction
 *
 */
@XObject("function")
public class FunctionDescriptor {

	@XNode("@class")
	private Class<? extends UfnxqlFunction> clazz;

	
	@XNode("@enabled")
	private boolean enabled = true;
	
	public FunctionDescriptor(){
		// do nothing
	}
	
	public Class<? extends UfnxqlFunction> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends UfnxqlFunction> clazz) {
		this.clazz = clazz;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public String toString(){
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("FunctionDescriptor : ");
	    strBuilder.append(clazz);	    
	    strBuilder.append("(");
	    strBuilder.append(isEnabled());
	    strBuilder.append(")");
	    return strBuilder.toString();
	}
	
}
