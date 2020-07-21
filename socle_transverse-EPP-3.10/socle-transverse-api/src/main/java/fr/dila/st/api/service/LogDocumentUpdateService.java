package fr.dila.st.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service de gestion de log des actions.
 * 
 * @author jtremeaux
 * @author arolin
 */
public interface LogDocumentUpdateService extends Serializable {

	/**
	 * Logge les modifications effectuée sur le document pour chacune des métadonnées modifiées.
	 * 
	 * @param session
	 * @param dossierDocument
	 * @throws ClientException
	 */
	void logAllDocumentUpdate(CoreSession session, DocumentModel dossierDocument) throws ClientException;
}
