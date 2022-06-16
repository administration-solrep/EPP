package fr.dila.ss.ui.bean;

import fr.dila.ss.api.recherche.IdLabel;
import java.util.ArrayList;
import java.util.List;

public class DernierElementDTO {
    private String id;
    private String label;
    private String categorie;
    private String exposant;
    private List<IdLabel> caseLinkIdsLabels = new ArrayList<>();

    public DernierElementDTO() {}

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

    public List<IdLabel> getCaseLinkIdsLabels() {
        return caseLinkIdsLabels;
    }

    public void setCaseLinkIdsLabels(List<IdLabel> caseLinkIdsLabels) {
        this.caseLinkIdsLabels = caseLinkIdsLabels;
    }

    public String getExposant() {
        return exposant;
    }

    public void setExposant(String exposant) {
        this.exposant = exposant;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
