package fr.dila.ss.core.service;

import fr.dila.st.core.service.STLockServiceImpl;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Implémentation du service de verrous du socle transverse.
 *
 * @author jtremeaux
 */
public class SSLockServiceImpl extends STLockServiceImpl {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SSLockServiceImpl() {
        super();
    }

    @Override
    protected void removeAllLock(DocumentModel document, CoreSession session) {
        FeuilleRoute docRoute = null;
        try {
            docRoute = document.getAdapter(FeuilleRoute.class);
        } catch (Exception e) {
            // ce n'est pas une route on fait rien
            return;
        }

        if (docRoute != null && docRoute.getAttachedDocuments() != null && docRoute.getAttachedDocuments().size() > 1) {
            for (String idAttachedDocument : docRoute.getAttachedDocuments()) {
                session.removeLock(new IdRef(idAttachedDocument));
                // abi ajouté dans la boucle pour éviter les ORA60
                session.save();
            }
        }
    }

    @Override
    protected void lockAll(DocumentModel document, CoreSession session) {
        FeuilleRoute docRoute = null;
        try {
            docRoute = document.getAdapter(FeuilleRoute.class);
        } catch (Exception e) {
            // ce n'est pas une route on fait rien
            return;
        }
        if (docRoute != null && docRoute.getAttachedDocuments() != null && docRoute.getAttachedDocuments().size() > 1) {
            final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
            for (String idAttachedDocument : docRoute.getAttachedDocuments()) {
                DocumentModel doc = uGet.getById(idAttachedDocument);
                DocumentRef docRef = doc.getRef();
                if (session.getLockInfo(docRef) == null) {
                    // new UnrestrictedLocker(docRef,session).runUnrestricted();
                    session.setLock(docRef);
                    // abi ajouté dans la boucle pour éviter les ORA60
                    session.save();
                }
            }
        }
    }
}
