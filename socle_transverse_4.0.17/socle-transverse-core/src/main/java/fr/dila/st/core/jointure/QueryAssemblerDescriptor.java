package fr.dila.st.core.jointure;

import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("queryassembler")
public class QueryAssemblerDescriptor {
    @XNode("@class")
    protected Class<UFNXQLQueryAssembler> klass;

    @XNode("@isDefault")
    protected Boolean isDefault;

    /**
     * Default constructor
     */
    public QueryAssemblerDescriptor() {
        // do nothing
    }

    public Class<UFNXQLQueryAssembler> getKlass() {
        return klass;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }
}
