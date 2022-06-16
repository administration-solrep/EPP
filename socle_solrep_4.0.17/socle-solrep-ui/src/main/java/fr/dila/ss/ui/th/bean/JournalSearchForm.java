package fr.dila.ss.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean
public class JournalSearchForm {
    @QueryParam("dateDebut")
    @FormParam("dateDebut")
    private String dateDebut;

    @QueryParam("dateFin")
    @FormParam("dateFin")
    private String dateFin;

    @QueryParam("utilisateur-key")
    @FormParam("utilisateur-key")
    private String utilisateurKey;

    private Map<String, String> mapUtilisateur = new HashMap<>();

    @QueryParam("categorie")
    @FormParam("categorie")
    private String categorie;

    @QueryParam("referenceDossier")
    @FormParam("referenceDossier")
    private String referenceDossier;

    public JournalSearchForm() {
        super();
    }

    public JournalSearchForm(String dateDebut, String dateFin, String categorie, String refDossier) {
        super();
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.categorie = categorie;
        this.referenceDossier = refDossier;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getUtilisateurKey() {
        return utilisateurKey;
    }

    public void setUtilisateurKey(String utilisateur) {
        this.utilisateurKey = utilisateur;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getReferenceDossier() {
        return referenceDossier;
    }

    public void setReferenceDossier(String referenceDossier) {
        this.referenceDossier = referenceDossier;
    }

    public Map<String, String> getMapUtilisateur() {
        return mapUtilisateur;
    }

    public void setMapUtilisateur(Map<String, String> mapUtilisateur) {
        this.mapUtilisateur = mapUtilisateur;
    }
}
