package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.domain.user.DelegationImpl;

/**
 * Fabrique des adapteurs de DocumentModel vers Delegation.
 * 
 * @author jtremeaux
 */
public class DelegationAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public DelegationAdapterFactory() {
		// do nothing
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new DelegationImpl(doc);
	}

	/**
	 * Vérifie que le document est de type
	 * 
	 * @param doc
	 */
	protected void checkDocument(DocumentModel doc) {
		if (!STConstant.DELEGATION_DOCUMENT_TYPE.equals(doc.getType())) {
			throw new CaseManagementRuntimeException("Document should be of type "
					+ STConstant.DELEGATION_DOCUMENT_TYPE);
		}
	}
}
