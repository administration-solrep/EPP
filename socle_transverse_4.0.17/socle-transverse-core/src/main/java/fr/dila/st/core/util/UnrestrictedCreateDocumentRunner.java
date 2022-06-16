package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Classe utilitaire permettant de récupérer un document par son ID, en désactivant la gestion des ACL.
 *
 * @author jtremeaux
 */
public class UnrestrictedCreateDocumentRunner extends UnrestrictedSessionRunner {
    /**
     * Document a créer
     */
    private DocumentModel doc;

    /**
     * Constructeur de UnrestrictedCreateDocumentRunner.
     *
     * @param session
     *            Session (restreinte)
     */
    public UnrestrictedCreateDocumentRunner(CoreSession session) {
        super(session);
    }

    @Override
    public void run() {
        doc = session.createDocument(doc);
        session.save();
    }

    /**
     * Crée un nouveau document en mode unrestricted.
     *
     * @param documentModel
     *            Document à créer
     * @return Document créé
     */
    public DocumentModel saveDocument(DocumentModel documentModel) {
        doc = documentModel;
        runUnrestricted();
        return doc;
    }
}
