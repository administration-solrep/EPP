package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class UniteStructurelleForm {
    @FormParam("identifiant")
    private String identifiant;

    @FormParam("type")
    private String type;

    @FormParam("libelle")
    private String libelle;

    @FormParam("dateDebut")
    private String dateDebut = SolonDateConverter.DATE_SLASH.formatNow();

    @FormParam("dateFin")
    private String dateFin;

    private HashMap<String, String> mapMinisteresRatachement = new HashMap<>();

    @FormParam("ministeres")
    private ArrayList<String> ministeresRatachement = new ArrayList<>();

    private HashMap<String, String> mapUnitesStructurellesRattachement = new HashMap<>();

    @FormParam("unitesStructurelles")
    private ArrayList<String> unitesStructurellesRattachement = new ArrayList<>();

    private HashMap<String, String> mapInstitution = new HashMap<>();

    @FormParam("institutions")
    private ArrayList<String> institutions = new ArrayList<>();

    private List<EntiteNorDirection> keyNors = new ArrayList<>();

    @FormParam("keyNors")
    private ArrayList<String> lstNors = new ArrayList<>();

    public UniteStructurelleForm() {}

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public HashMap<String, String> getMapMinisteresRatachement() {
        return new HashMap<>(mapMinisteresRatachement);
    }

    public void setMapMinisteresRatachement(HashMap<String, String> mapMinisteresRatachement) {
        this.mapMinisteresRatachement = new HashMap<>(mapMinisteresRatachement);
    }

    public List<String> getMinisteresRatachement() {
        return new ArrayList<>(ministeresRatachement);
    }

    public void setMinisteresRatachement(ArrayList<String> ministeresRatachement) {
        this.ministeresRatachement = new ArrayList<>(ministeresRatachement);
    }

    public HashMap<String, String> getMapUnitesStructurellesRattachement() {
        return new HashMap<>(mapUnitesStructurellesRattachement);
    }

    public void setMapUnitesStructurellesRattachement(HashMap<String, String> mapUnitesStructurellesRattachement) {
        this.mapUnitesStructurellesRattachement = new HashMap<>(mapUnitesStructurellesRattachement);
    }

    public List<String> getUnitesStructurellesRattachement() {
        return new ArrayList<>(unitesStructurellesRattachement);
    }

    public void setUnitesStructurellesRattachement(ArrayList<String> unitesStructurellesRattachement) {
        this.unitesStructurellesRattachement = new ArrayList<>(unitesStructurellesRattachement);
    }

    public HashMap<String, String> getMapInstitution() {
        return new HashMap<>(mapInstitution);
    }

    public void setMapInstitution(HashMap<String, String> mapInstitution) {
        this.mapInstitution = new HashMap<>(mapInstitution);
    }

    public ArrayList<String> getInstitutions() {
        return new ArrayList<>(institutions);
    }

    public void setInstitutions(ArrayList<String> institutions) {
        this.institutions = new ArrayList<>(institutions);
    }

    public List<EntiteNorDirection> getKeyNors() {
        return keyNors;
    }

    public void setKeyNors(List<EntiteNorDirection> keyNors) {
        this.keyNors = new ArrayList<>(keyNors);
    }

    public ArrayList<String> getLstNors() {
        return new ArrayList<>(lstNors);
    }

    public void setLstNors(ArrayList<String> lstNors) {
        this.lstNors = new ArrayList<>(lstNors);
    }
}
