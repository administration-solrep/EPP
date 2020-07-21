package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.administration.EtatApplicationImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet File.
 * 
 * @author feo
 */
public class EtatApplicationAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public EtatApplicationAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.ETAT_APPLICATION_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should have schema "
					+ STSchemaConstant.ETAT_APPLICATION_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new EtatApplicationImpl(doc);
	}
}
