package fr.dila.ss.api.criteria;

import java.util.List;

public class SubstitutionCriteria {
    private List<String> listIdMinistereAttributaire;
    private String typeActe;

    public SubstitutionCriteria() {}

    public SubstitutionCriteria(List<String> listIdMinistereAttributaire) {
        this.listIdMinistereAttributaire = listIdMinistereAttributaire;
    }

    public SubstitutionCriteria(List<String> listIdMinistereAttributaire, String typeActe) {
        this.listIdMinistereAttributaire = listIdMinistereAttributaire;
        this.typeActe = typeActe;
    }

    public List<String> getListIdMinistereAttributaire() {
        return listIdMinistereAttributaire;
    }

    public void setListIdMinistereAttributaire(List<String> listIdMinistereAttributaire) {
        this.listIdMinistereAttributaire = listIdMinistereAttributaire;
    }

    public String getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(String typeActe) {
        this.typeActe = typeActe;
    }
}
