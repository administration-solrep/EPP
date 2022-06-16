package fr.dila.st.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.ws.rs.FormParam;

@SwBean
public class UserSearchForm {
    @FormParam("utilisateur")
    private String utilisateur;

    @FormParam("prenom")
    private String prenom;

    @FormParam("nom")
    private String nom;

    @FormParam("mel")
    private String mel;

    @FormParam("telephone")
    private String telephone;

    private HashMap<String, String> mapMinisteres = new HashMap<>();

    @FormParam("ministeres")
    private ArrayList<String> ministeres = new ArrayList<>();

    private HashMap<String, String> mapUnitesStructurelles = new HashMap<>();

    @FormParam("unitesStructurelles")
    private ArrayList<String> unitesStructurelles = new ArrayList<>();

    private HashMap<String, String> mapPostes = new HashMap<>();

    @FormParam("postes")
    private ArrayList<String> postes;

    @FormParam("profils")
    private ArrayList<String> profils;

    @FormParam("dateCreationDebut")
    private String dateCreationDebut;

    @FormParam("dateCreationFin")
    private String dateCreationFin;

    @FormParam("dateExpirationDebut")
    private String dateExpirationDebut;

    @FormParam("dateExpirationFin")
    private String dateExpirationFin;

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMel() {
        return mel;
    }

    public void setMel(String mel) {
        this.mel = mel;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<String> getProfils() {
        return profils;
    }

    public void setProfils(ArrayList<String> profils) {
        this.profils = profils;
    }

    public String getDateCreationDebut() {
        return dateCreationDebut;
    }

    public void setDateCreationDebut(String dateCreationDebut) {
        this.dateCreationDebut = dateCreationDebut;
    }

    public String getDateCreationFin() {
        return dateCreationFin;
    }

    public void setDateCreationFin(String dateCreationFin) {
        this.dateCreationFin = dateCreationFin;
    }

    public String getDateExpirationDebut() {
        return dateExpirationDebut;
    }

    public void setDateExpirationDebut(String dateExpirationDebut) {
        this.dateExpirationDebut = dateExpirationDebut;
    }

    public String getDateExpirationFin() {
        return dateExpirationFin;
    }

    public void setDateExpirationFin(String dateExpirationFin) {
        this.dateExpirationFin = dateExpirationFin;
    }

    public List<String> getPostes() {
        return postes;
    }

    public void setPostes(ArrayList<String> postes) {
        this.postes = postes;
    }

    public HashMap<String, String> getMapPostes() {
        return mapPostes;
    }

    public void setMapPostes(HashMap<String, String> mapPostes) {
        this.mapPostes = mapPostes;
    }

    public HashMap<String, String> getMapMinisteres() {
        return mapMinisteres;
    }

    public void setMapMinisteres(HashMap<String, String> mapMinisteres) {
        this.mapMinisteres = mapMinisteres;
    }

    public List<String> getMinisteres() {
        return ministeres;
    }

    public void setMinisteres(ArrayList<String> ministeres) {
        this.ministeres = ministeres;
    }

    public HashMap<String, String> getMapUnitesStructurelles() {
        return mapUnitesStructurelles;
    }

    public void setMapUnitesStructurelles(HashMap<String, String> mapUnitesStructurelles) {
        this.mapUnitesStructurelles = mapUnitesStructurelles;
    }

    public List<String> getUnitesStructurelles() {
        return unitesStructurelles;
    }

    public void setUnitesStructurelles(ArrayList<String> unitesStructurelles) {
        this.unitesStructurelles = unitesStructurelles;
    }

    public boolean hasFieldsNull() {
        return Stream
            .of(
                utilisateur,
                prenom,
                nom,
                mel,
                postes,
                ministeres,
                unitesStructurelles,
                telephone,
                profils,
                dateCreationFin,
                dateCreationDebut,
                dateExpirationFin,
                dateExpirationDebut
            )
            .allMatch(Objects::isNull);
    }
}
