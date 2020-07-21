package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.user.BaseFunctionImpl;

/**
 * Fabrique des adapteurs de DocumentModel vers BaseFunction.
 * 
 * @author jtremeaux
 */
public class BaseFunctionAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public BaseFunctionAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.BASE_FUNCTION_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.BASE_FUNCTION_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new BaseFunctionImpl(doc);
	}
}
