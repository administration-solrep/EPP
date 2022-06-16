package fr.dila.ss.core.tree;

import fr.dila.ss.api.tree.SSTreeNode;
import fr.dila.st.core.domain.STDomainObjectImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSTreeNodeImpl extends STDomainObjectImpl implements SSTreeNode {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -4023784460581681664L;
    protected int depth = 0;

    public SSTreeNodeImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getId() {
        return document.getId();
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String getType() {
        return document.getType();
    }

    @Override
    public String getName() {
        return document.getName();
    }
}
