package fr.sword.naiad.nuxeo.ufnxql.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 *  &lt;mixinType name="ma_facette"/&gt;
 *  
 * @author SPL
 *
 */
@XObject("mixinType")
public class MixinTypeDescriptor {

	@XNode("@name")
	private String name;
	
	/**
	 * Default constructor
	 */
	public MixinTypeDescriptor(){
		// do nothing
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "MixinTypeDescriptor["+name+"]";
	}
}
