package fr.dila.solonepp.api.dao.criteria;

/**
 * Critères de recherche des pièces jointes.
 * 
 * @author jtremeaux
 */
public class PieceJointeCriteria {
    /**
     * UUID différente de cette valeur.
     */
    private String idNot;
    
    /**
     * La pièce jointe contient l'UUID de ce fichier de pièce jointe.
     */
    private String pieceJointeFichierId;

    
    private String typePieceJointe;
    
    private String idVersion;
    
    /**
     * Getter de idNot.
     *
     * @return idNot
     */
    public String getIdNot() {
        return idNot;
    }

    /**
     * Setter de idNot.
     *
     * @param idNot idNot
     */
    public void setIdNot(String idNot) {
        this.idNot = idNot;
    }

    /**
     * Getter de pieceJointeFichierId.
     *
     * @return pieceJointeFichierId
     */
    public String getPieceJointeFichierId() {
        return pieceJointeFichierId;
    }

    /**
     * Setter de pieceJointeFichierId.
     *
     * @param pieceJointeFichierId pieceJointeFichierId
     */
    public void setPieceJointeFichierId(String pieceJointeFichierId) {
        this.pieceJointeFichierId = pieceJointeFichierId;
    }

    /**
     * @return the typePieceJointe
     */
    public String getTypePieceJointe() {
        return typePieceJointe;
    }

    /**
     * @param typePieceJointe the typePieceJointe to set
     */
    public void setTypePieceJointe(String typePieceJointe) {
        this.typePieceJointe = typePieceJointe;
    }

    /**
     * @return the idVersion
     */
    public String getIdVersion() {
        return idVersion;
    }

    /**
     * @param idVersion the idVersion to set
     */
    public void setIdVersion(String idVersion) {
        this.idVersion = idVersion;
    }
    
}
