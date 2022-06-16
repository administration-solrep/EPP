package fr.dila.ss.core.fondDeDossier;

import fr.dila.ss.api.fondDeDossier.SSFondDeDossierNode;
import fr.dila.ss.core.tree.SSTreeNodeImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSFondDeDossierNodeImpl extends SSTreeNodeImpl implements SSFondDeDossierNode {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SSFondDeDossierNodeImpl(DocumentModel doc) {
        super(doc);
    }
}
