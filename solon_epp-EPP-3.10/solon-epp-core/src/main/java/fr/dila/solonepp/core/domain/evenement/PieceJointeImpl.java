package fr.dila.solonepp.core.domain.evenement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

import java.util.Collections;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier pièce jointe.
 * 
 * @author jtremeaux
 */
public class PieceJointeImpl implements PieceJointe {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de PieceJointeImpl.
     * 
     * @param document Modèle de document
     */
    public PieceJointeImpl(DocumentModel document) {
        this.document = document;
    }
    
    @Override
    public String getTitle() {
    	return DublincoreSchemaUtils.getTitle(document);
    }
    
    @Override
    public void setTitle(String title) {
    	DublincoreSchemaUtils.setTitle(document, title);
    }
    
    @Override
    public String getTypePieceJointe() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_TYPE_PIECE_JOINTE_PROPERTY);
    }

    @Override
    public void setTypePieceJointe(String typePieceJointe) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_TYPE_PIECE_JOINTE_PROPERTY,
                typePieceJointe);
    }

    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_NOM_PROPERTY);
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_NOM_PROPERTY,
                nom);
    }

    @Override
    public String getUrl() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_URL_PROPERTY);
    }

    @Override
    public void setUrl(String url) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_URL_PROPERTY,
                url);
    }

    @Override
    public List<String> getPieceJointeFichierList() {
        return PropertyUtil.getStringListProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY);
    }

    @Override
    public void setPieceJointeFichierList(List<String> pieceJointeFichierList) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY,
                pieceJointeFichierList);
    }
    
    @Override
    public List<String> getModifiedMetaList() {
        String value = PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_MODIFIED_META_LIST_PROPERTY);
        List<String> list =  new ArrayList<String>();
        if (value != null) {
            Collections.addAll(list, value.split(";"));
        }
        return list;
    }

    @Override
    public void setModifiedMetaList(List<String> modifiedMetaList) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_MODIFIED_META_LIST_PROPERTY,
                StringUtils.join(modifiedMetaList,";"));
    }

    @Override
    public List<String> getModifiedFileList() {
        String value = PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_MODIFIED_FILE_LIST_PROPERTY);
        List<String> list =  new ArrayList<String>();
        if (value != null) {
            Collections.addAll(list, value.split(";"));
        }
        return list;
    }

    @Override
    public void setModifiedFileList(List<String> modifiedFileList) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_MODIFIED_FILE_LIST_PROPERTY,
                StringUtils.join(modifiedFileList,";"));
    }

    @Override
    public List<String> getDeletedFileList() {
        String value = PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_DELETED_FILE_LIST_PROPERTY);
        List<String> list =  new ArrayList<String>();
        if (value != null) {
            Collections.addAll(list, value.split(";"));
        }
        return list;
    }

    @Override
    public void setDeletedFileList(List<String> deletedFileList) {
        String value = null;
        if (deletedFileList != null && !deletedFileList.isEmpty()) {
            value = StringUtils.join(deletedFileList,";");
        }
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA,
                SolonEppSchemaConstant.PIECE_JOINTE_DELETED_FILE_LIST_PROPERTY,
                value);
    }
    
    // *************************************************************
    // Données de contexte (non persistées).
    // *************************************************************
    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentModel> getPieceJointeFichierDocList() {
        return (List<DocumentModel>) document.getContextData(SolonEppConstant.PIECE_JOINTE_FICHIER_CONTEXT);
    }
    
    @Override
    public void setPieceJointeFichierDocList(List<DocumentModel> pieceJointeFichierList) {
        document.putContextData(SolonEppConstant.PIECE_JOINTE_FICHIER_CONTEXT, (Serializable) pieceJointeFichierList);
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }


}
