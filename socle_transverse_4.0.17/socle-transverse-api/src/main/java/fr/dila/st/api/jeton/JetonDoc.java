package fr.dila.st.api.jeton;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapter pour les documents de type JetonDoc.
 *
 * @author spesnel
 */
public interface JetonDoc extends Serializable {
    /**
     * Retourne le modèle de document.
     */
    DocumentModel getDocument();

    void saveDocument(CoreSession session);

    // *************************************************************
    // Propriétés du document JetonDoc.
    // *************************************************************
    /**
     * Retourne l'UUID du document associé au jeton.
     *
     * @return UUID du document associé au jeton.
     */
    String getIdDoc();

    /**
     * Renseigne l'UUID du document associé au jeton.
     *
     * @param idDoc
     */
    void setIdDoc(String idDoc);

    /**
     * Retourne le numéro du jeton.
     *
     * @return numéro du jeton
     */
    Long getNumeroJeton();

    /**
     * Renseigne le numéro du jeton.
     *
     * @param numeroJeton
     *            numéro du jeton
     */
    void setNumeroJeton(Long numeroJeton);

    /**
     * Retourne le type de webservice pour lequel ce jeton est dédié.
     *
     * @return Type de webservice pour lequel ce jeton est dédié
     */
    String getTypeWebservice();

    /**
     * Renseigne le type de webservice pour lequel ce jeton est dédié.
     *
     * @param type
     *            Type de webservice pour lequel ce jeton est dédié
     */
    void setTypeWebservice(String type);

    /**
     * Retourne l'identifiant du proprietaire pour lequel ce jeton est dédié (ID d'un noeud de l'organigramme).
     *
     * @return Identifiant du proprietaire pour lequel ce jeton est dédié (ID d'un noeud de l'organigramme)
     */
    String getIdOwner();

    /**
     * Renseigne l'identifiant du proprietaire pour lequel ce jeton est dédié (ID d'un noeud de l'organigramme).
     *
     * @param type
     *            Identifiant du proprietaire pour lequel ce jeton est dédié (ID d'un noeud de l'organigramme)
     */
    void setIdOwner(String type);

    /**
     * Renseigne la date de création du jeton
     *
     * @param created la date de création du jeton
     */
    void setCreated(Calendar created);

    /**
     * Récupère la date de création du jeton
     *
     * @return la date de création du jeton
     */
    Calendar getCreated();

    /**
     * Récupère le type de modification correspondant à la création du jeton
     *
     * @return le type de modification
     */
    String getTypeModification();

    /**
     * Renseigne le type de modification correspondant à la création du jeton
     *
     * @param typeModification le type de modification
     */
    void setTypeModification(String typeModification);

    /**
     * Récupère les ids complémentaires pour le jeton
     *
     * @return les ids complémentaires
     */
    List<String> getIdsComplementaires();

    /**
     * Renseigne les ids complémentaires pour le jeton
     *
     * @param idsComplementaires les ids complémentaires
     */
    void setIdsComplementaires(List<String> idsComplementaires);
}
