package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Classe utilitaire permettant de récupérer un document par son ID, en désactivant la gestion des ACL.
 * 
 * @author jtremeaux
 */
public class UnrestrictedGetDocumentRunner extends UnrestrictedSessionRunner {
	/**
	 * Référence du document.
	 */
	private DocumentRef			docRef;

	/**
	 * Résultats de recherche.
	 */
	private DocumentModel		doc;

	/**
	 * Résultats de recherche si on recherche les fils d'un document.
	 */
	private DocumentModelList	docModelList;

	/**
	 * Indique si l'on effectue une recherche sur les fils d'un document.
	 */
	private boolean				isChildrenSearch	= false;

	/**
	 * Constructeur de UnrestrictedQueryRunner.
	 * 
	 * @param session
	 *            Session (restreinte)
	 */
	public UnrestrictedGetDocumentRunner(CoreSession session) {
		super(session);
	}

	@Override
	public void run() throws ClientException {
		if (isChildrenSearch) {
			docModelList = session.getChildren(docRef);
		} else {
			doc = session.getDocument(docRef);
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
	public DocumentModel getByRef(DocumentRef docReference) throws ClientException {
		docRef = docReference;
		runUnrestricted();
		return doc;
	}

	/**
	 * Recherche un document par son identifiant technique (UUID) et retourne le document. Retourne null si aucun
	 * document n'est trouvé.
	 * 
	 * @param uuidDocument
	 *            Identifiant technique du document (UUID)
	 * @return Document ou null
	 * @throws ClientException
	 */
	public DocumentModel getById(String uuidDocument) throws ClientException {
		return getByRef(new IdRef(uuidDocument));
	}

	/**
	 * Recherche une liste de document fils par l'identifiant technique (UUID) d'un document et retourne cette liste.
	 * Retourne null si aucun document n'est trouvé.
	 * 
	 * @param uuidDocument
	 *            Identifiant technique du document (UUID)
	 * @return Document List ou null
	 * @throws ClientException
	 */
	public DocumentModelList getChildrenById(String uuidDocument) throws ClientException {
		return getChildrenByRef(new IdRef(uuidDocument));
	}

	/**
	 * Recherche une liste de document fils par la référence d'un document et retourne cette liste. Retourne null si
	 * aucun document n'est trouvé.
	 * 
	 * @param docReference
	 *            Référence du document
	 * @return Document List ou null
	 * @throws ClientException
	 */
	public DocumentModelList getChildrenByRef(DocumentRef docReference) throws ClientException {
		// on signale que l'on effectue une recherche sur les éléments fils d'un document
		isChildrenSearch = true;
		docRef = docReference;
		// on effectue la requete
		runUnrestricted();
		return docModelList;
	}

	/**
	 * Recherche un document par son chemin et retourne le document. Retourne null si aucun document n'est trouvé.
	 * 
	 * @param path
	 *            Chemin du document
	 * @return Document ou null
	 * @throws ClientException
	 */
	public DocumentModel getByPath(String path) throws ClientException {
		return getByRef(new PathRef(path));
	}
}
