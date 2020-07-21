package fr.dila.solonepp.core.adapter.message;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.core.domain.message.MessageImpl;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier Message.
 * 
 * @author jtremeaux
 */
public class MessageAdapterFactory implements DocumentAdapterFactory {
	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.CASE_LINK_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.CASE_LINK_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new MessageImpl(doc);
	}
}
