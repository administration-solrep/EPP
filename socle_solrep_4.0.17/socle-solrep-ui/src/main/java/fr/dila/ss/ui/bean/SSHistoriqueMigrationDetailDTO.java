package fr.dila.ss.ui.bean;

import java.util.List;

public class SSHistoriqueMigrationDetailDTO {

    public SSHistoriqueMigrationDetailDTO() {
        super();
    }

    private String id;

    private String label;

    private List<SSHistoriqueMigrationDetailLigneDTO> lignes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<SSHistoriqueMigrationDetailLigneDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<SSHistoriqueMigrationDetailLigneDTO> lignes) {
        this.lignes = lignes;
    }
}
