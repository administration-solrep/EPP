package fr.dila.st.api.domain.comment;

import fr.dila.st.api.domain.STDomainObject;
import java.util.Calendar;

/**
 * Interface du type Comment
 *
 */
public interface STComment extends STDomainObject {
    /**
     * getter de l'auteur du commentaire
     *
     * @return auteur du commentaire
     */
    String getAuthor();

    /**
     * setter de l'auteur du commentaire
     *
     */
    void setAuthor(String author);

    /**
     * getter du texte du commentaire
     *
     * @return texte du commentaire
     */
    String getTexte();

    /**
     * setter du texte du commentaire
     *
     */
    void setTexte(String text);

    /**
     * getter de la date de creation du commentaire
     *
     * @return date de creation du commentaire
     */
    Calendar getCreationDate();

    /**
     * setter de la date de creation du commentaire
     *
     */
    void setCreationDate(Calendar creationDate);

    /**
     * getter du parent id du commentaire
     *
     * @return parent id du commentaire
     */
    String getParentCommentId();
}
