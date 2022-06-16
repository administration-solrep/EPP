package fr.dila.st.ui.th.bean;

import com.google.common.base.Joiner;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import java.util.stream.Collectors;

@SwBean(keepdefaultValue = true)
public class UserForm {
    @FormParam("nom")
    private String nom;

    @FormParam("prenom")
    private String prenom;

    @FormParam("utilisateur")
    private String utilisateur;

    @FormParam("fonction")
    private String fonction;

    @FormParam("mel")
    private String mel;

    @FormParam("dateDebut")
    private String dateDebut;

    @FormParam("civilite")
    private String civilite;

    @FormParam("temporaire")
    private String temporaire;

    @FormParam("dateFin")
    private String dateFin;

    @FormParam("adresse")
    private String adresse;

    @FormParam("codePostal")
    private String codePostal;

    @FormParam("ville")
    private String ville;

    @FormParam("telephone")
    private String telephone;

    @FormParam("profils")
    private ArrayList<String> profils;

    private HashMap<String, String> mapPostes = new HashMap<>();

    @FormParam("postes")
    private ArrayList<String> postes;

    @FormParam("ministeres")
    private String ministeres;

    private Calendar dateConnexion;

    private boolean occasionnel;

    public UserForm() {
        super();
    }

    public UserForm(String nom, String prenom, String utilisateur, String mel, String dateDebut) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.utilisateur = utilisateur;
        this.mel = mel;
        this.dateDebut = dateDebut;
    }

    /**
     * Renvoi une chaine sous la forme "NOM Prenom (utilisateur)"
     *
     * @return une chaine sous la forme "NOM Prenom (utilisateur)"
     */
    public String getFullNameIdentifier() {
        Joiner joiner = Joiner.on(" ").skipNulls();

        return joiner.join(nom, prenom, utilisateur);
    }

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

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getMel() {
        return mel;
    }

    public void setMel(String mel) {
        this.mel = mel;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getCivilite() {
        return civilite;
    }

    public void setCivilite(String civilite) {
        this.civilite = civilite;
    }

    public String getTemporaire() {
        return temporaire;
    }

    public void setTemporaire(String temporaire) {
        this.temporaire = temporaire;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<String> getProfils() {
        return ObjectHelper.requireNonNullElseGet(profils, Collections::emptyList);
    }

    public void setProfils(ArrayList<String> profils) {
        this.profils = profils;
    }

    public List<String> getPostes() {
        return postes;
    }

    public void setPostes(ArrayList<String> postes) {
        this.postes = postes;
    }

    public String getMinisteres() {
        return ministeres;
    }

    public void setMinisteres(List<String> ministeres) {
        this.ministeres = ministeres.stream().collect(Collectors.joining(";"));
    }

    public Map<String, String> getMapPostes() {
        return mapPostes;
    }

    public void setMapPostes(HashMap<String, String> mapPostes) {
        this.mapPostes = mapPostes;
    }

    public Calendar getDateConnexion() {
        return dateConnexion;
    }

    public void setDateConnexion(Calendar dateConnexion) {
        this.dateConnexion = dateConnexion;
    }

    public boolean isOccasionnel() {
        return occasionnel;
    }

    public void setOccasionnel(boolean occasionnel) {
        this.occasionnel = occasionnel;
    }
}
