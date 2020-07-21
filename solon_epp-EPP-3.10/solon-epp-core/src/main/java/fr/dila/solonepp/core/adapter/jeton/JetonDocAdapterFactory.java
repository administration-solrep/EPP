package fr.dila.solonepp.core.adapter.jeton;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.core.domain.jeton.JetonDocImpl;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier JetonDoc.
 * 
 * @author jtremeaux
 */
public class JetonDocAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.JETON_DOCUMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.JETON_DOCUMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new JetonDocImpl(doc);
	}
}
