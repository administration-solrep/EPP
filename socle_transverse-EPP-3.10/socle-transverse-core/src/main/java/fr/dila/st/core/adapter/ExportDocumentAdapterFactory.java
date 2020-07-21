package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.core.domain.ExportDocumentImpl;

/**
 * Adapteur de Document vers ExportDocument.
 * 
 */
public class ExportDocumentAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constrcutor
	 */
	public ExportDocumentAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STExportConstants.EXPORT_DOC_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STExportConstants.EXPORT_DOC_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new ExportDocumentImpl(doc);
	}
}
