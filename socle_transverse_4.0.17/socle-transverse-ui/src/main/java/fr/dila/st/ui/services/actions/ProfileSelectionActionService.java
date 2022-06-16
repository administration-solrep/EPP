package fr.dila.st.ui.services.actions;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface ProfileSelectionActionService {
    /**
     * Ajout d'un profil à la liste des profils de l'utilisateur.
     *
     * @param userDoc
     *            Document utilisateur
     */
    void addProfile(DocumentModel userDoc, String profile);

    /**
     * Retrait d'un profil à la liste des profils de l'utilisateur.
     *
     * @param userDoc
     *            Document utilisateur
     * @param profile
     *            Profil à retirer
     */
    void removeProfile(DocumentModel userDoc, String profile);
}
