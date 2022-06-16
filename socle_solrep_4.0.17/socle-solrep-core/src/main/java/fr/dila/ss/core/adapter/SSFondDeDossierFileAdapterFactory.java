package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFileImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers SSTreeFolder.
 *
 */
public class SSFondDeDossierFileAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public SSFondDeDossierFileAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, SSTreeConstants.FILE_SCHEMA);
        return new SSFondDeDossierFileImpl(doc);
    }
}
