package fr.dila.ss.ui.services.comment;

import fr.dila.st.ui.th.model.SpecificContext;

public interface SSCommentManagerUIService {
    void addComment(SpecificContext context);

    void addCommentRunningStep(SpecificContext context);

    void addCommentFromDossierLinks(SpecificContext context);

    void addSharedComment(SpecificContext context);

    void updateComment(SpecificContext context);

    /**
     * On reste en mode showWidgetComment sur le même routeStep
     */
    void deleteComment(SpecificContext context);

    boolean isVisible(SpecificContext context);

    /**
     * FEV514 : Une condition suffisante pour pouvoir supprimer une note est @Override
	d'être dans un des postes de son auteur.
     *
     * @return true si l'utilisateur courant est dans le même poste que l'auteur du commentaire
     */
    boolean isInAuthorPoste(SpecificContext context);

    boolean isAuthor(SpecificContext context);
}
