package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Classe utilitaire permettant de récupérer un document par son ID, en désactivant la gestion des ACL.
 *
 * @author jtremeaux
 */
public class UnrestrictedCreateOrSaveDocumentRunner extends UnrestrictedSessionRunner {
    /**
     * Document a créer
     */
    private DocumentModel doc;

    private Boolean forCreation;

    /**
     * Constructeur de UnrestrictedCreateDocumentRunner.
     *
     * @param session
     *            Session (restreinte)
     */
    public UnrestrictedCreateOrSaveDocumentRunner(CoreSession session) {
        super(session);
    }

    @Override
    public void run() {
        if (forCreation) {
            doc = session.createDocument(doc);
        } else {
            doc = session.saveDocument(doc);
        }
        session.save();
    }

    /**
     * Recherche un document par sa référence et retourne le document. Retourne null si aucun document n'est trouvé.
     *
     * @param docReference
     *            Référence du document
     * @return Document ou null
     *
     */
    public DocumentModel createDocument(DocumentModel documentModel) {
        forCreation = true;
        doc = documentModel;
        runUnrestricted();
        return doc;
    }

    public DocumentModel saveDocument(DocumentModel documentModel) {
        forCreation = false;
        doc = documentModel;
        runUnrestricted();
        return doc;
    }
}
