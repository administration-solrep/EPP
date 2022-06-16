package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.core.domain.ExportDocumentImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de Document vers ExportDocument.
 *
 */
public class ExportDocumentAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constrcutor
     */
    public ExportDocumentAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STExportConstants.EXPORT_DOC_SCHEMA);
        return new ExportDocumentImpl(doc);
    }
}
