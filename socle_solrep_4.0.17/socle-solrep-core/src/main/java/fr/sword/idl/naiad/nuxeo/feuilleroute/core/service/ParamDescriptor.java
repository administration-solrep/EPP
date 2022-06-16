package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 *
 */
@XObject("parameter")
public class ParamDescriptor {
    @XNode("@name")
    private String name;

    @XNode("@value")
    private String value;

    public ParamDescriptor() {
        // do nothing
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
