package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class PosteForm {
    @FormParam("id")
    private String id;

    @FormParam("libelle")
    private String libelle;

    @FormParam("conseiller")
    private String conseiller;

    @FormParam("superviseur")
    private String superviseur;

    @FormParam("chargeMission")
    private String chargeMission;

    @FormParam("posteBDC")
    private String posteBDC;

    @FormParam("dateDebut")
    private String dateDebut = SolonDateConverter.DATE_SLASH.formatNow();

    @FormParam("dateFin")
    private String dateFin;

    private HashMap<String, String> mapUnitesStructurellesRattachement = new HashMap<>();

    @FormParam("unitesStructurelles")
    private ArrayList<String> unitesStructurellesRattachement = new ArrayList<>();

    private HashMap<String, String> mapInstitution = new HashMap<>();

    @FormParam("institutions")
    private ArrayList<String> institutions = new ArrayList<>();

    public PosteForm() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuperviseur() {
        return superviseur;
    }

    public void setSuperviseur(String superviseur) {
        this.superviseur = superviseur;
    }

    public String getChargeMission() {
        return chargeMission;
    }

    public void setChargeMission(String chargeMission) {
        this.chargeMission = chargeMission;
    }

    public String getPosteBDC() {
        return posteBDC;
    }

    public void setPosteBDC(String posteBDC) {
        this.posteBDC = posteBDC;
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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public HashMap<String, String> getMapUnitesStructurellesRattachement() {
        return mapUnitesStructurellesRattachement;
    }

    public void setMapUnitesStructurellesRattachement(HashMap<String, String> mapUnitesStructurellesRattachement) {
        this.mapUnitesStructurellesRattachement = mapUnitesStructurellesRattachement;
    }

    public List<String> getUnitesStructurellesRattachement() {
        return unitesStructurellesRattachement;
    }

    public void setUnitesStructurellesRattachement(ArrayList<String> unitesStructurellesRattachement) {
        this.unitesStructurellesRattachement = unitesStructurellesRattachement;
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

    public String getConseiller() {
        return conseiller;
    }

    public void setConseiller(String conseiller) {
        this.conseiller = conseiller;
    }
}
