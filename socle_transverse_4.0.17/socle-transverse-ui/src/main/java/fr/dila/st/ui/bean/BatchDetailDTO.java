package fr.dila.st.ui.bean;

import java.util.Date;

public class BatchDetailDTO {
    private String nom;
    private String type;
    private Date debut;
    private Date fin;
    private Integer erreurs;
    private long resultat;
    private long duree;
    private String message;

    public BatchDetailDTO() {
        super();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDebut() {
        return debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Integer getErreurs() {
        return erreurs;
    }

    public void setErreurs(Integer erreurs) {
        this.erreurs = erreurs;
    }

    public long getResultat() {
        return resultat;
    }

    public void setResultat(long resultat) {
        this.resultat = resultat;
    }

    public long getDuree() {
        return duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
