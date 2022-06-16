package fr.dila.epp.ui.th.bean;

import com.google.gson.Gson;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean
public class RapidSearchForm {
    @FormParam("idCorbeille")
    private String idCorbeille;

    @FormParam("typeCommunication")
    private ArrayList<String> typeCommunication;

    @FormParam("idDossier")
    private String idDossier;

    @FormParam("objetDossier")
    private String objetDossier;

    @FormParam("idCommunication")
    private String idCommunication;

    @FormParam("dateCommunicationDebut")
    private String dateDebut;

    @FormParam("dateCommunicationFin")
    private String dateFin;

    @FormParam("emetteur")
    private ArrayList<String> emetteur;

    @FormParam("destinataire")
    private ArrayList<String> destinataire;

    @FormParam("copie")
    private ArrayList<String> copie;

    public RapidSearchForm() {
        super();
    }

    @SuppressWarnings("unchecked")
    public RapidSearchForm(String json) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);

        if (map != null) {
            setIdCorbeille((String) map.get("idCorbeille"));
            setTypeCommunication((ArrayList<String>) map.get("typeCommunication"));
            setIdDossier((String) map.get("idDossier"));
            setObjetDossier((String) map.get("objetDossier"));
            setIdCommunication((String) map.get("idCommunication"));
            setDateDebut((String) map.get("dateCommunicationDebut"));
            setDateFin((String) map.get("dateCommunicationFin"));
            setEmetteur((ArrayList<String>) map.get("emetteur"));
            setDestinataire((ArrayList<String>) map.get("destinataire"));
            setCopie((ArrayList<String>) map.get("copie"));
        }
    }

    public String getIdCommunication() {
        return idCommunication;
    }

    public void setIdCommunication(String idCommunication) {
        this.idCommunication = idCommunication;
    }

    public String getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    public ArrayList<String> getTypeCommunication() {
        return typeCommunication;
    }

    public void setTypeCommunication(ArrayList<String> typeCommunication) {
        this.typeCommunication = typeCommunication;
    }

    public String getObjetDossier() {
        return objetDossier;
    }

    public void setObjetDossier(String objetDossier) {
        this.objetDossier = objetDossier;
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

    public ArrayList<String> getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(ArrayList<String> emetteur) {
        this.emetteur = emetteur;
    }

    public ArrayList<String> getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(ArrayList<String> destinataire) {
        this.destinataire = destinataire;
    }

    public ArrayList<String> getCopie() {
        return copie;
    }

    public void setCopie(ArrayList<String> copie) {
        this.copie = copie;
    }

    public String getIdCorbeille() {
        return idCorbeille;
    }

    public void setIdCorbeille(String idCorbeille) {
        this.idCorbeille = idCorbeille;
    }
}
