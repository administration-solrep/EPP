package fr.sword.idl.naiad.nuxeo.addons.status.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("status")
public class StatusDescriptor {

    @XNode("@clazz")
    private String clazz;

    @XNode("@name")
    private String name;

    @XNode("@params")
    private String params;

    @XNode("@enabled")
    private Boolean enabled;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(final String clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public String getParams() {
        return params;
    }

    public void setParams(final String params) {
        this.params = params;
    }
}
