package fr.dila.st.core.helper;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.core.service.STServiceLocator;

public final class ParameterTestHelper {

	/**
	 * utility class
	 */
	private ParameterTestHelper() {
		// do nothing
	}

	public static STParametre changeOrCreateParammeter(final CoreSession session, String name, String value)
			throws ClientException {
		STParametre param = STServiceLocator.getSTParametreService().getParametre(session, name);
		if (param != null) {
			param.setValue(value);
			DocumentModel doc = session.saveDocument(param.getDocument());
			return doc.getAdapter(STParametre.class);
		} else {
			return createParameter(session, name, value);
		}
	}

	/**
	 * crée un parametre
	 * 
	 * @param session
	 * @param name
	 * @param value
	 * @return
	 * @throws ClientException
	 */
	private static STParametre createParameter(final CoreSession session, String name, String value)
			throws ClientException {
		DocumentModel doc = session.createDocumentModel("/", name, STConstant.PARAMETRE_DOCUMENT_TYPE);
		STParametre param = doc.getAdapter(STParametre.class);
		param.setValue(value);
		doc = session.createDocument(doc);
		return doc.getAdapter(STParametre.class);
	}

}
