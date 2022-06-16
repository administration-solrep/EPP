package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class PosteWsForm {

    public PosteWsForm() {}

    @FormParam("id")
    private String id;

    @FormParam("libelle")
    private String libelle;

    @FormParam("urlWs")
    private String urlWs;

    @FormParam("utilisateurWs")
    private String utilisateurWs;

    @FormParam("mdpWs")
    private String mdpWs;

    @FormParam("keystore")
    private String keystore;

    @FormParam("dateDebut")
    private String dateDebut = SolonDateConverter.DATE_SLASH.formatNow();

    @FormParam("dateFin")
    private String dateFin;

    private HashMap<String, String> mapMinisteres;

    @FormParam("ministeres")
    private ArrayList<String> ministeres = new ArrayList<>();

    private HashMap<String, String> mapInstitution = new HashMap<>();

    @FormParam("institutions")
    private ArrayList<String> institutions = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getUrlWs() {
        return urlWs;
    }

    public void setUrlWs(String urlWs) {
        this.urlWs = urlWs;
    }

    public String getUtilisateurWs() {
        return utilisateurWs;
    }

    public void setUtilisateurWs(String utilisateurWs) {
        this.utilisateurWs = utilisateurWs;
    }

    public String getMdpWs() {
        return mdpWs;
    }

    public void setMdpWs(String mdpWs) {
        this.mdpWs = mdpWs;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
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

    public HashMap<String, String> getMapInstitution() {
        return mapInstitution;
    }

    public void setMapInstitution(HashMap<String, String> mapInstitution) {
        this.mapInstitution = mapInstitution;
    }

    public ArrayList<String> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(ArrayList<String> institutions) {
        this.institutions = institutions;
    }
}
