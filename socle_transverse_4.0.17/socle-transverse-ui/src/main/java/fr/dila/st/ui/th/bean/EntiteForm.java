package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class EntiteForm {
    @FormParam("identifiant")
    private String identifiant;

    @FormParam("appellation")
    private String appellation;

    @FormParam("edition")
    private String edition;

    @FormParam("dateDebut")
    private String dateDebut = SolonDateConverter.DATE_SLASH.formatNow();

    @FormParam("dateFin")
    private String dateFin;

    @FormParam("gouvernement")
    private OrganigrammeElementDTO gouvernement;

    @FormParam("idGouvernement")
    private String idGouvernement;

    @FormParam("ordreProtocolaire")
    private String ordreProtocolaire;

    @FormParam("formulesEntetes")
    private String formulesEntetes;

    @FormParam("nor")
    private String nor;

    @FormParam("civilite")
    private String civilite;

    @FormParam("membreGouvernementNom")
    private String membreGouvernementNom;

    @FormParam("membreGouvernementPrenom")
    private String membreGouvernementPrenom;

    @FormParam("suiviAN")
    private Boolean suiviAN;

    public EntiteForm() {
        super();
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
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

    public OrganigrammeElementDTO getGouvernement() {
        return gouvernement;
    }

    public void setGouvernement(OrganigrammeElementDTO gouvernement) {
        this.gouvernement = gouvernement;
    }

    public String getIdGouvernement() {
        return idGouvernement;
    }

    public void setIdGouvernement(String idGouvernement) {
        this.idGouvernement = idGouvernement;
    }

    public String getOrdreProtocolaire() {
        return ordreProtocolaire;
    }

    public void setOrdreProtocolaire(String ordreProtocolaire) {
        this.ordreProtocolaire = ordreProtocolaire;
    }

    public String getFormulesEntetes() {
        return formulesEntetes;
    }

    public void setFormulesEntetes(String formulesEntetes) {
        this.formulesEntetes = formulesEntetes;
    }

    public String getCivilite() {
        return civilite;
    }

    public void setCivilite(String civilite) {
        this.civilite = civilite;
    }

    public String getMembreGouvernementNom() {
        return membreGouvernementNom;
    }

    public void setMembreGouvernementNom(String membreGouvernementNom) {
        this.membreGouvernementNom = membreGouvernementNom;
    }

    public String getMembreGouvernementPrenom() {
        return membreGouvernementPrenom;
    }

    public void setMembreGouvernementPrenom(String membreGouvernementPrenom) {
        this.membreGouvernementPrenom = membreGouvernementPrenom;
    }

    public String getNor() {
        return nor;
    }

    public void setNor(String nor) {
        this.nor = nor;
    }

    public Boolean getSuiviAN() {
        return suiviAN;
    }

    public void setSuiviAN(Boolean suiviAN) {
        this.suiviAN = suiviAN;
    }
}
