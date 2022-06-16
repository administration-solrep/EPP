package fr.dila.ss.core.fondDeDossier;

import fr.dila.ss.api.fondDeDossier.SSFondDeDossierFile;
import fr.dila.ss.core.tree.SSTreeFileImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSFondDeDossierFileImpl extends SSTreeFileImpl implements SSFondDeDossierFile {
    /**
     *
     */
    private static final long serialVersionUID = -6499443072170078721L;

    public SSFondDeDossierFileImpl(DocumentModel doc) {
        super(doc);
    }
}
