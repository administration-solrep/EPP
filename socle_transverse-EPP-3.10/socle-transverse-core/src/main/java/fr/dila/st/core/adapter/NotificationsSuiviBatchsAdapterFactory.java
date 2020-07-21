package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.administration.NotificationsSuiviBatchsImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet NotificationsSuiviBatchs.
 * 
 * @author feo
 */
public class NotificationsSuiviBatchsAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public NotificationsSuiviBatchsAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should have schema "
					+ STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new NotificationsSuiviBatchsImpl(doc);
	}
}
