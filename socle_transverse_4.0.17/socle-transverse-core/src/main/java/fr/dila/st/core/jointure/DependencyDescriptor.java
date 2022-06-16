package fr.dila.st.core.jointure;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Le descripteur pour une dépendance, utilisé dans le service jointure lier les liens entre deux correspondances.
 *
 * @author jgomez
 *
 */
@XObject("dependency")
public class DependencyDescriptor {
    @XNode("@prefix")
    private String prefix;

    @XNode("@need")
    private String need;

    /**
     * Default constructor
     */
    public DependencyDescriptor() {
        // do nothing
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }
}
