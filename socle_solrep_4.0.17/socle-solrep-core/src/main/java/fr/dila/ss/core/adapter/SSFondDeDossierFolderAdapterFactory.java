package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFolderImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers SSTreeFolder.
 *
 */
public class SSFondDeDossierFolderAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public SSFondDeDossierFolderAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentFacet(doc, SSTreeConstants.FOLDERISH_FACET);
        return new SSFondDeDossierFolderImpl(doc);
    }
}
