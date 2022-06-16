package fr.sword.naiad.nuxeo.commons.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * ACE Descriptor.
 */
@XObject(value = "ace")
public class AceDescriptor {

    @XNode("@granted")
    private boolean granted = true;

    @XNode("@principal")
    private String principal;

    @XNode("@permission")
    private String permission;
    
    public AceDescriptor(){
    	// do nothing
    }
    
    public AceDescriptor(String principal, String permission, boolean granted){
    	this.principal = principal;
    	this.permission = permission;
    	this.granted = granted;
    }

    public boolean isGranted() {
        return granted;
    }

    public String getPermission() {
        return permission;
    }

    public String getPrincipal() {
        return principal;
    }

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

    
}
