package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.file.FileImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet File.
 * 
 * @author arolin
 */
public class FileAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public FileAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.FILE_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should have schema " + STSchemaConstant.FILE_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new FileImpl(doc);
	}
}
