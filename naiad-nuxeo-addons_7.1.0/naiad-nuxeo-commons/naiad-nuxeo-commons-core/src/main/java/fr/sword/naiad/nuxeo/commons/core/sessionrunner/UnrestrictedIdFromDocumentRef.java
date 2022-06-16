package fr.sword.naiad.nuxeo.commons.core.sessionrunner;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Helper permettant de récupérer l'identifiant d'un document à partir d'une référence, dans une session non restreinte.
 * 
 * @author fmh
 */
public class UnrestrictedIdFromDocumentRef extends UnrestrictedSessionRunner {
	private final DocumentRef documentRef;

	private String documentId = null;

	/**
	 * Construit l'objet à partir d'une session et d'une référence à un document.
	 * 
	 * @param session
	 *            Session Nuxeo, restreinte ou non.
	 * @param documentRef
	 *            Référence à un document.
	 */
	public UnrestrictedIdFromDocumentRef(CoreSession session, DocumentRef documentRef) {
		super(session);
		this.documentRef = documentRef;
	}

	@Override
	public void run() throws NuxeoException {
		if (documentRef == null) {
			throw new NuxeoException("Null document reference");
		}

		DocumentModel document = session.getDocument(documentRef);

		if (document != null) {
			documentId = document.getId();
		}
	}

	public String getDocumentId() {
		return documentId;
	}
}
