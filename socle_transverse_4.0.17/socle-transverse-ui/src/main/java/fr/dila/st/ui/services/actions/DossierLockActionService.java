package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface DossierLockActionService {
    /**
     * Verrouille le dossier et la feuille de route associée.
     *
     * @param session
     * @param dossierDoc
     */
    void lockDossier(SpecificContext context, CoreSession session, DocumentModel dossierDoc);

    /**
     * Verrouille le dossier chargé et la feuille de route associée.
     *
     * @param context
     */
    void lockCurrentDossier(SpecificContext context);

    /**
     * Déverrouille le dossier et la feuille de route associée.
     *
     * @param session
     * @param dossierDoc
     */
    void unlockDossier(SpecificContext context);

    /**
     * Déverrouille le dossier en cours et la feuille de route associée.
     *
     * @param context
     */
    void unlockCurrentDossier(SpecificContext context);

    /**
     * Détermine si l'utilisateur peut déverrouiller le dossier.
     *
     * @param session
     * @param dossier
     * @return condition
     */
    boolean getCanUnlockDossier(CoreSession session, DocumentModel dossier);

    /**
     * Détermine si l'utilisateur peut déverrouiller le dossier chargé.
     *
     * @param context
     * @return condition
     */
    boolean getCanUnlockCurrentDossier(SpecificContext context);

    /**
     * Détermine si l'utilisateur peut verrouiller le dossier.
     *
     * @param dossier
     *            Dossier
     * @return Condition
     */
    boolean getCanLockDossier(DocumentModel dossier, CoreSession session);

    /**
     * Détermine si l'utilisateur peut verrouiller le dossier chargé.
     *
     * @return Condition
     */
    boolean getCanLockCurrentDossier(SpecificContext context);
}
