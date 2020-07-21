package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.core.alert.AlertImpl;

/**
 * Adapteur de Document vers Alert.
 * 
 * @author jgomez
 */
public class AlertAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constrcutor
	 */
	public AlertAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STAlertConstant.ALERT_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema " + STAlertConstant.ALERT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new AlertImpl(doc);
	}
}
