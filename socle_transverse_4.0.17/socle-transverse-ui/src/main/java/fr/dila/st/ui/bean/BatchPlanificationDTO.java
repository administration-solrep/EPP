package fr.dila.st.ui.bean;

import java.util.Date;

public class BatchPlanificationDTO {
    private String nom;
    private Date dateDernierDeclenchement;
    private Date dateProchainDeclenchement;
    private String periodicite;

    public BatchPlanificationDTO() {
        super();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateDernierDeclenchement() {
        return dateDernierDeclenchement;
    }

    public void setDateDernierDeclenchement(Date dateDernierDeclenchement) {
        this.dateDernierDeclenchement = dateDernierDeclenchement;
    }

    public Date getDateProchainDeclenchement() {
        return dateProchainDeclenchement;
    }

    public void setDateProchainDeclenchement(Date dateProchainDeclenchement) {
        this.dateProchainDeclenchement = dateProchainDeclenchement;
    }

    public String getPeriodicite() {
        return periodicite;
    }

    public void setPeriodicite(String periodicite) {
        this.periodicite = periodicite;
    }
}
