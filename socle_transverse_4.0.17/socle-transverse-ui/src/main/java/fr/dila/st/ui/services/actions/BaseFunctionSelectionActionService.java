package fr.dila.st.ui.services.actions;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface BaseFunctionSelectionActionService {
    /**
     * Ajout d'un profil à la liste des profils de l'utilisateur.
     *
     * @param profileDoc
     *            Document profil
     * @param baseFunction
     */
    void addBaseFunction(DocumentModel profileDoc, String baseFunction);

    /**
     * Retrait d'une fonction unitaire à la liste des fonctions unitaires d'un profil.
     *
     * @param profileDoc
     *            Document profil
     * @param baseFunction
     *            Fonction unitaire à retirer
     */
    void removeBaseFunction(DocumentModel profileDoc, String baseFunction);
}
