package fr.dila.st.ui.services.actions.impl;

import static org.nuxeo.ecm.core.api.security.SecurityConstants.EVERYTHING;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.WRITE_PROPERTIES;

import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class DossierLockActionServiceImpl implements DossierLockActionService {
    private static final String LOCKER = "document.locker";

    private static final String LOCK_CREATED = "document.lock.created";

    @Override
    public void lockDossier(SpecificContext context, CoreSession session, DocumentModel dossierDoc) {
        List<DocumentModel> documents = new ArrayList<>();
        STDossier dossier = dossierDoc.getAdapter(STDossier.class);

        documents.add(dossierDoc);
        String lastRouteId = dossier.getLastDocumentRoute();
        if (lastRouteId != null) {
            documents.add(session.getDocument(new IdRef(lastRouteId)));
        }

        STActionsServiceLocator
            .getSTLockActionService()
            .lockDocuments(context, session, documents, dossierDoc.getType());
    }

    @Override
    public void lockCurrentDossier(SpecificContext context) {
        lockDossier(context, context.getSession(), context.getCurrentDocument());
    }

    @Override
    public void unlockDossier(SpecificContext context) {
        List<DocumentModel> documents = new ArrayList<>();
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        if (dossierDoc != null) {
            STDossier dossier = dossierDoc.getAdapter(STDossier.class);

            documents.add(dossierDoc);
            String lastRouteId = dossier.getLastDocumentRoute();
            if (lastRouteId != null) {
                documents.add(session.getDocument(new IdRef(lastRouteId)));
            }

            STActionsServiceLocator
                .getSTLockActionService()
                .unlockDocumentsUnrestricted(context, documents, dossierDoc.getType());
        }
    }

    @Override
    public void unlockCurrentDossier(SpecificContext context) {
        unlockDossier(context);
    }

    @Override
    public boolean getCanUnlockDossier(CoreSession session, DocumentModel dossier) {
        boolean canUnlock = false;
        if (dossier == null) {
            canUnlock = false;
        } else {
            NuxeoPrincipal userName = session.getPrincipal();
            Map<String, Serializable> lockDetails = new HashMap<>();
            Lock lock = session.getLockInfo(dossier.getRef());
            if (lock != null) {
                lockDetails.put(LOCKER, lock.getOwner());
                lockDetails.put(LOCK_CREATED, lock.getCreated());
            }
            if (lockDetails.isEmpty() || dossier.isProxy()) {
                canUnlock = false;
            } else {
                canUnlock =
                    (
                        (userName.isAdministrator() || session.hasPermission(dossier.getRef(), EVERYTHING))
                            ? true
                            : (
                                userName.getName().equals(lockDetails.get(LOCKER)) &&
                                session.hasPermission(dossier.getRef(), WRITE_PROPERTIES)
                            )
                    ) &&
                    !dossier.isVersion();
            }
        }
        return canUnlock;
    }

    @Override
    public boolean getCanUnlockCurrentDossier(SpecificContext context) {
        return getCanUnlockDossier(context.getSession(), context.getCurrentDocument());
    }

    /**
     * Détermine si l'utilisateur peut verrouiller le dossier.
     *
     * @param dossier
     *            Dossier
     * @return Condition
     */
    @Override
    public boolean getCanLockDossier(DocumentModel dossier, CoreSession session) {
        return STActionsServiceLocator.getSTLockActionService().getCanLockDoc(dossier, session);
    }

    /**
     * Détermine si l'utilisateur peut verrouiller le dossier chargé.
     *
     * @return Condition
     */
    @Override
    public boolean getCanLockCurrentDossier(SpecificContext context) {
        return getCanLockDossier(context.getCurrentDocument(), context.getSession());
    }
}
