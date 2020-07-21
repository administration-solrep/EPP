package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.user.MockSTUser;

/**
 * Fabrique des adapteurs de DocumentModel vers STUser.
 * 
 */
public class MockSTUserAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public MockSTUserAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.USER_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema " + STSchemaConstant.USER_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new MockSTUser(doc);
	}
}
