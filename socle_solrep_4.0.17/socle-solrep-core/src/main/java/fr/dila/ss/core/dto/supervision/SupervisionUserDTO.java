package fr.dila.ss.core.dto.supervision;

import java.util.Calendar;

public class SupervisionUserDTO {
    private String nom;
    private String prenom;
    private String utilisateur;
    private Calendar dateConnexion;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Calendar getDateConnexion() {
        return dateConnexion;
    }

    public void setDateConnexion(Calendar dateConnexion) {
        this.dateConnexion = dateConnexion;
    }
}
