package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.tree.SSTreeNodeImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers SSTreeFolder.
 *
 * @author jtremeaux
 */
public class SSTreeNodeAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public SSTreeNodeAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentFacet(doc, SSTreeConstants.FOLDERISH_FACET);
        checkDocumentSchemas(doc, SSTreeConstants.FILE_SCHEMA);
        return new SSTreeNodeImpl(doc);
    }
}
