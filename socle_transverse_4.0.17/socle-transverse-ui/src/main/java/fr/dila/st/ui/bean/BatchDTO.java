package fr.dila.st.ui.bean;

import java.util.Date;

public class BatchDTO {
    private String id;
    private String nom;
    private Date debut;
    private Date fin;
    private Long erreurs;
    private Integer tomcat;
    private String type;
    private String serveur;

    public BatchDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public Long getErreurs() {
        return erreurs;
    }

    public void setErreurs(Long erreurs) {
        this.erreurs = erreurs;
    }

    public Integer getTomcat() {
        return tomcat;
    }

    public void setTomcat(Integer tomcat) {
        this.tomcat = tomcat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServeur() {
        return serveur;
    }

    public void setServeur(String serveur) {
        this.serveur = serveur;
    }
}
