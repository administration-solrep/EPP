package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.tree.SSTreeFolderImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers SSTreeFolder.
 *
 * @author jtremeaux
 */
public class SSTreeFolderAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public SSTreeFolderAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentFacet(doc, SSTreeConstants.FOLDERISH_FACET);
        return new SSTreeFolderImpl(doc);
    }
}
