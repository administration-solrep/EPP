package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.file.FileImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet File.
 *
 * @author arolin
 */
public class FileAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public FileAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.FILE_SCHEMA);
        return new FileImpl(doc);
    }
}
