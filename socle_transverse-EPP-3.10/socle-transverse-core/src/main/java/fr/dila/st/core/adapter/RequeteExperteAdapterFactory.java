package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.requeteur.RequeteExperteImpl;

/**
 * @author jgomez
 */
public class RequeteExperteAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public RequeteExperteAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!(doc.hasSchema(STRequeteConstants.SMART_FOLDER_SCHEMA))) {
			throw new CaseManagementRuntimeException("Document should contain schema smart_folder");
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new RequeteExperteImpl(doc);
	}

}
