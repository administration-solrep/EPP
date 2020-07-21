package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.user.ProfileImpl;

/**
 * Fabrique des adapteurs de DocumentModel vers Profile.
 * 
 * @author jtremeaux
 */
public class ProfileAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public ProfileAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.GROUP_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema " + STSchemaConstant.GROUP_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new ProfileImpl(doc);
	}
}
