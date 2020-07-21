package fr.dila.solonepp.api.dto;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface PieceJointeDTO extends Serializable{

	String getPieceJointeId();
	
	void setPieceJointeId(String pieceJointeId);
	
	List<DocumentModel> getListPieceJointeFichier();
	
	void setListPieceJointeFichier(List<DocumentModel> listPieceJointeFichier);

	void addPieceJointeFichier(DocumentModel pieceJointeFichier);
	
	String getPieceJointeTitre();
	
	void setPieceJointeTitre(String pieceJointeTitre);
	
	String getPieceJointeUrl();
	
	void setPieceJointeUrl(String pieceJointeUrl);
	
    /**
     * @return the titreModified
     */
    boolean isTitreModified();

    /**
     * @return the urlModified
     */
    boolean isUrlModified();

    boolean isFileModified(String file);
    
    List<String> getDeletedFileList();
}
