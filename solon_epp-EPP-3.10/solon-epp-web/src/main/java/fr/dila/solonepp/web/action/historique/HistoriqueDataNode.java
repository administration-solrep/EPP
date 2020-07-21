package fr.dila.solonepp.web.action.historique;

public class HistoriqueDataNode {

    private String label;
    private String idEvenement;
    private Boolean evenementCourant = false;
    private Boolean annuler = false;
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the evenementCourant
     */
    public Boolean getEvenementCourant() {
        return evenementCourant;
    }

    /**
     * @param evenementCourant the evenementCourant to set
     */
    public void setEvenementCourant(Boolean evenementCourant) {
        this.evenementCourant = evenementCourant;
    }

    public HistoriqueDataNode(String label, Boolean evenementCourant, Boolean annuler, String idEvenement) {
        super();
        this.label = label;
        this.evenementCourant = evenementCourant;
        this.idEvenement = idEvenement;
        this.annuler = annuler;
    }

    public void setIdEvenement(String idEvenement) {
        this.idEvenement = idEvenement;
    }

    public String getIdEvenement() {
        return idEvenement;
    }

    /**
     * @return the annuler
     */
    public Boolean getAnnuler() {
        return annuler;
    }

    /**
     * @param annuler the annuler to set
     */
    public void setAnnuler(Boolean annuler) {
        this.annuler = annuler;
    }
}
