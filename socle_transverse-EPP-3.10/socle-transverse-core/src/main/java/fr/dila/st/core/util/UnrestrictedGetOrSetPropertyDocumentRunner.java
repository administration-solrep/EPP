package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Classe utilitaire permettant de récupérer un document par son ID, en désactivant la gestion des ACL.
 * 
 * @author jtremeaux
 */
public class UnrestrictedGetOrSetPropertyDocumentRunner extends UnrestrictedSessionRunner {

	private String				schema;
	private String				property;
	private Object				object;
	private final DocumentModel	doc;

	private Boolean				getterCalled;

	/**
	 * Constructeur de UnrestrictedCreateDocumentRunner.
	 * 
	 * @param session
	 *            Session (restreinte)
	 */
	public UnrestrictedGetOrSetPropertyDocumentRunner(CoreSession session, DocumentModel doc) {
		super(session);
		this.doc = doc;
	}

	@Override
	public void run() throws ClientException {
		if (getterCalled) {
			object = doc.getProperty(schema, property);
		} else {
			doc.setProperty(schema, property, object);
		}
	}

	/**
	 * Recherche un document par sa référence et retourne le document. Retourne null si aucun document n'est trouvé.
	 * 
	 * @param docReference
	 *            Référence du document
	 * @return Document ou null
	 * @throws ClientException
	 */
	public DocumentModel setProperty(String schema, String property, Object object) throws ClientException {
		getterCalled = false;
		this.schema = schema;
		this.property = property;
		this.object = object;
		runUnrestricted();
		return doc;
	}

	public Object getProperty(String schema, String property) throws ClientException {
		getterCalled = true;
		this.schema = schema;
		this.property = property;
		runUnrestricted();
		return object;
	}

}
