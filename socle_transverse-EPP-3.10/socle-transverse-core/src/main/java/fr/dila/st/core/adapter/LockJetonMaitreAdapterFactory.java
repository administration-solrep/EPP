package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.jeton.LockJetonMaitreImpl;

public class LockJetonMaitreAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public LockJetonMaitreAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new LockJetonMaitreImpl(doc);
	}
}
