package fr.dila.st.core.requete.recherchechamp.descriptor;

import java.io.Serializable;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("parametre")
public class ParametreDescriptor implements Serializable {
    private static final long serialVersionUID = -7154955364476610731L;

    @XNode("@name")
    private String name;

    @XNode("@value")
    private String value;

    public ParametreDescriptor() {}

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
