package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.parametre.STParametreImpl;

/**
 * 
 * Fabrique d'adapteur de DocumentModel vers l'objet STParametre.
 * 
 */
public class ParametreAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public ParametreAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.PARAMETRE_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.PARAMETRE_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new STParametreImpl(doc);
	}
}
