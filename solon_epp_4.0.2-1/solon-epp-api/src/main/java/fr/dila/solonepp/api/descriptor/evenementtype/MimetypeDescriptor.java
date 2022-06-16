package fr.dila.solonepp.api.descriptor.evenementtype;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("mimetype")
public class MimetypeDescriptor {
    @XNode("@value")
    private String value;

    @XNode("@label")
    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
