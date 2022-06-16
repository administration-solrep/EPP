package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class PieceJointeDTO {
    private String pieceJointeId;
    private String pieceJointeTitre;
    private String pieceJointeUrl;
    private String pieceJointeType;
    private boolean isNew;

    private List<String> modifiedMetaList;
    private List<String> modifiedFileList;
    private List<String> deletedFileList;

    private List<DocumentDTO> listPieceJointeFichier = new ArrayList<>();

    public PieceJointeDTO() {
        super();
    }

    public String getPieceJointeId() {
        return pieceJointeId;
    }

    public void setPieceJointeId(String pieceJointeId) {
        this.pieceJointeId = pieceJointeId;
    }

    public String getPieceJointeTitre() {
        return pieceJointeTitre;
    }

    public void setPieceJointeTitre(String pieceJointeTitre) {
        this.pieceJointeTitre = pieceJointeTitre;
    }

    public String getPieceJointeUrl() {
        return pieceJointeUrl;
    }

    public void setPieceJointeUrl(String pieceJointeUrl) {
        this.pieceJointeUrl = pieceJointeUrl;
    }

    public String getPieceJointeType() {
        return pieceJointeType;
    }

    public void setPieceJointeType(String pieceJointeType) {
        this.pieceJointeType = pieceJointeType;
    }

    public List<String> getModifiedMetaList() {
        return modifiedMetaList;
    }

    public void setModifiedMetaList(List<String> modifiedMetaList) {
        this.modifiedMetaList = modifiedMetaList;
    }

    public List<String> getModifiedFileList() {
        return modifiedFileList;
    }

    public void setModifiedFileList(List<String> modifiedFileList) {
        this.modifiedFileList = modifiedFileList;
    }

    public List<String> getDeletedFileList() {
        return deletedFileList;
    }

    public void setDeletedFileList(List<String> deletedFileList) {
        this.deletedFileList = deletedFileList;
    }

    public List<DocumentDTO> getListPieceJointeFichier() {
        return listPieceJointeFichier;
    }

    public void setListPieceJointeFichier(List<DocumentDTO> listPieceJointeFichier) {
        this.listPieceJointeFichier = listPieceJointeFichier;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
