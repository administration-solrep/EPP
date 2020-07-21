package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.jeton.JetonMaitreImpl;

/**
 * @author arolin
 */
public class JetonMaitreAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public JetonMaitreAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.JETON_MAITRE_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ STSchemaConstant.JETON_MAITRE_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new JetonMaitreImpl(doc);
	}

}
