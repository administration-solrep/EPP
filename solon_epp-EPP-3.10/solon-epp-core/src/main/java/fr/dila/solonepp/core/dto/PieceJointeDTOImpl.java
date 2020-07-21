package fr.dila.solonepp.core.dto;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dto.PieceJointeDTO;

/**
 * DTO pour les pieces jointes
 * @author asatre
 *
 */
public class PieceJointeDTOImpl implements PieceJointeDTO {

	private static final long serialVersionUID = 2156213617937821214L;
	
	private String pieceJointeId;
	private String pieceJointeTitre;
	private String pieceJointeUrl;
	
	private List<String> modifiedMetaList;
	private List<String> modifiedFileList;
	private List<String> deletedFileList;
	
	private List<DocumentModel> listPieceJointeFichier;
	
	
	private PieceJointeDTOImpl(){
		this.listPieceJointeFichier = new ArrayList<DocumentModel>();
	}
	
	public PieceJointeDTOImpl(String pieceJointeId, String pieceJointeTitre, String pieceJointeUrl, List<String> modifiedMetaList, List<String> modifiedFileList, List<String> deletedFileList){
		this();
		this.pieceJointeId = pieceJointeId;
		this.pieceJointeTitre = pieceJointeTitre; 
		this.pieceJointeUrl = pieceJointeUrl;
		this.modifiedMetaList = modifiedMetaList;
		this.modifiedFileList = modifiedFileList;
		this.deletedFileList = deletedFileList;
	}
	
	@Override
	public String getPieceJointeId() {
		return pieceJointeId;
	}

	@Override
	public void setPieceJointeId(String pieceJointeId) {
		this.pieceJointeId = pieceJointeId;
	}

	@Override
	public List<DocumentModel> getListPieceJointeFichier() {
		return listPieceJointeFichier;
	}

	@Override
	public void setListPieceJointeFichier(List<DocumentModel> listPieceJointeFichier) {
		this.listPieceJointeFichier = listPieceJointeFichier;
	}
	
	@Override
	public void addPieceJointeFichier(DocumentModel pieceJointeFichier) {
		this.listPieceJointeFichier.add(pieceJointeFichier);
	}

	@Override
	public String getPieceJointeTitre() {
		return pieceJointeTitre;
	}

	@Override
	public void setPieceJointeTitre(String pieceJointeTitre) {
		this.pieceJointeTitre = pieceJointeTitre;
	}

	@Override
	public String getPieceJointeUrl() {
		return pieceJointeUrl;
	}

	@Override
	public void setPieceJointeUrl(String pieceJointeUrl) {
		this.pieceJointeUrl = pieceJointeUrl;
	}

    @Override
    public boolean isTitreModified() {
        if (modifiedMetaList == null) {
            return false;
        }
        return modifiedMetaList.contains(SolonEppSchemaConstant.PIECE_JOINTE_NOM_PROPERTY);
    }

    @Override
    public boolean isUrlModified() {
        if (modifiedMetaList == null) {
            return false;
        }
        return modifiedMetaList.contains(SolonEppSchemaConstant.PIECE_JOINTE_URL_PROPERTY);
    }

    @Override
    public boolean isFileModified(String file) {
        if (modifiedFileList == null) {
            return false;
        }
        return modifiedFileList.contains(file);
    }

    /**
     * @return the deletedFileList
     */
    public List<String> getDeletedFileList() {
        return deletedFileList;
    }

    /**
     * @param deletedFileList the deletedFileList to set
     */
    public void setDeletedFileList(List<String> deletedFileList) {
        this.deletedFileList = deletedFileList;
    }
    
}
