package fr.dila.solonepp.api.domain.evenement;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des objets métiers pièce jointe.
 * 
 * @author jtremeaux
 */
public interface PieceJointe extends Serializable {

    /**
     * Retourne le modèle de document.
     */
    DocumentModel getDocument();
    
    /**
     * Retourne le titre de la pièce jointe.
     * 
     * @return Titre de la pièce jointe
     */
    String getTitle();
    
    /**
     * Renseigne le titre de la pièce jointe.
     * 
     * @param title Titre de la pièce jointe
     */
    void setTitle(String title);

    /**
     * Retourne le type de pièce jointe.
     * 
     * @return Type de pièce jointe
     */
    String getTypePieceJointe();
    
    /**
     * Renseigne le type de pièce jointe.
     * 
     * @param typePieceJointe Type de pièce jointe
     */
    void setTypePieceJointe(String typePieceJointe);

    /**
     * Retourne le libellé de la pièce jointe.
     * 
     * @return Libellé de la pièce jointe
     */
    String getNom();
    
    /**
     * Renseigne le libellé de la pièce jointe.
     * 
     * @param nom Libellé de la pièce jointe
     */
    void setNom(String nom);

    /**
     * Retourne l'URL vers le site Web de l'émetteur.
     * 
     * @return URL vers le site Web de l'émetteur
     */
    String getUrl();
    
    /**
     * Renseigne l'URL vers le site Web de l'émetteur.
     * 
     * @param url URL vers le site Web de l'émetteur
     */
    void setUrl(String url);

    /**
     * Retourne la liste des fichiers joints.
     * 
     * @return Liste des fichiers joints
     */
    List<String> getPieceJointeFichierList();
    
    /**
     * Renseigne la liste des fichiers joints.
     * 
     * @param pieceJointeFichierList Liste des fichiers joints
     */
    void setPieceJointeFichierList(List<String> pieceJointeFichierList);

    /**
     * @return modifiedMetaList
     */
    List<String> getModifiedMetaList();
    
    /**
     * @param modifiedMetaList set modifiedMetaList
     */
    void setModifiedMetaList(List<String> modifiedMetaList);
    
    /**
     * @return modifiedFileList
     */
    List<String> getModifiedFileList();
    
    /**
     * @param modifiedFileList set modifiedFileList
     */
    void setModifiedFileList(List<String> modifiedFileList);
  
    /**
     * @return deletedFileList
     */
    List<String> getDeletedFileList();
    
    /**
     * @param deletedFileList set deletedFileList
     */
    void setDeletedFileList(List<String> deletedFileList);
    
    // *************************************************************
    // Données de contexte (non persistées).
    // *************************************************************
    /**
     * Retourne la liste des documents fichiers de pièces jointes.
     * /!\ Cette propriété est utilisée pour transférer des documents, mais il faut charger les documents manuellement.
     * 
     * @return Liste des documents fichiers de pièces jointes.
     */
    List<DocumentModel> getPieceJointeFichierDocList();
    
    /**
     * Renseigne la liste des documents fichiers de pièces jointes.
     * /!\ Cette propriété est utilisée pour transférer des documents, mais il faut charger les documents manuellement.
     * 
     * @param pieceJointeFichierList Liste des documents fichiers de pièces jointes.
     */
    void setPieceJointeFichierDocList(List<DocumentModel> pieceJointeFichierList);
}
