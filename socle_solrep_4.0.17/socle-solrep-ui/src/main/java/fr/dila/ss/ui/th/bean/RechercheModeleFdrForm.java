package fr.dila.ss.ui.th.bean;

import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.st.ui.annot.SwBean;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean
public class RechercheModeleFdrForm {
    @FormParam("intitule_modele")
    private String intitule;

    @FormParam("numero_modele")
    private String numero;

    @FormParam("typeActe")
    private String typeActe;

    @FormParam("ministere_modele-key")
    private String ministere;

    private Map<String, String> mapMinistere = new HashMap<>();

    @FormParam("direction_modele-key")
    private String direction;

    private Map<String, String> mapDirection = new HashMap<>();

    @FormParam("utilisateurCreateur-key")
    private String utilisateurCreateur;

    private Map<String, String> mapUtilisateurCreateur = new HashMap<>();

    @FormParam("dateCreationDebut")
    private Calendar dateCreationStart;

    @FormParam("dateCreationFin")
    private Calendar dateCreationEnd;

    @FormParam("statut")
    private StatutModeleFDR statut;

    @FormParam("typeEtape")
    private String typeEtape;

    @FormParam("destinataire-key")
    private String destinataire;

    private Map<String, String> mapDestinataire = new HashMap<>();

    @FormParam("echeanceIndicative")
    private String echeanceIndicative;

    @FormParam("franchissementAutomatique")
    private Boolean franchissementAutomatique;

    @FormParam("obligatoireSGG")
    private Boolean obligatoireSGG;

    @FormParam("obligatoireMinistere")
    private Boolean obligatoireMinistere;

    public RechercheModeleFdrForm() {
        super();
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(String typeActe) {
        this.typeActe = typeActe;
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getUtilisateurCreateur() {
        return utilisateurCreateur;
    }

    public void setUtilisateurCreateur(String utilisateurCreateur) {
        this.utilisateurCreateur = utilisateurCreateur;
    }

    public Calendar getDateCreationStart() {
        return dateCreationStart;
    }

    public void setDateCreationStart(Calendar dateCreationStart) {
        this.dateCreationStart = dateCreationStart;
    }

    public Calendar getDateCreationEnd() {
        return dateCreationEnd;
    }

    public void setDateCreationEnd(Calendar dateCreationEnd) {
        this.dateCreationEnd = dateCreationEnd;
    }

    public StatutModeleFDR getStatut() {
        return statut;
    }

    public void setStatut(StatutModeleFDR statut) {
        this.statut = statut;
    }

    public String getTypeEtape() {
        return typeEtape;
    }

    public void setTypeEtape(String typeEtape) {
        this.typeEtape = typeEtape;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public String getEcheanceIndicative() {
        return echeanceIndicative;
    }

    public void setEcheanceIndicative(String echeanceIndicative) {
        this.echeanceIndicative = echeanceIndicative;
    }

    public Boolean getFranchissementAutomatique() {
        return franchissementAutomatique;
    }

    public void setFranchissementAutomatique(Boolean franchissementAutomatique) {
        this.franchissementAutomatique = franchissementAutomatique;
    }

    public Boolean getObligatoireSGG() {
        return obligatoireSGG;
    }

    public void setObligatoireSGG(Boolean obligatoireSGG) {
        this.obligatoireSGG = obligatoireSGG;
    }

    public Boolean getObligatoireMinistere() {
        return obligatoireMinistere;
    }

    public void setObligatoireMinistere(Boolean obligatoireMinistere) {
        this.obligatoireMinistere = obligatoireMinistere;
    }

    public Map<String, String> getMapUtilisateurCreateur() {
        return mapUtilisateurCreateur;
    }

    public void setMapUtilisateurCreateur(Map<String, String> mapUtilisateurCreateur) {
        this.mapUtilisateurCreateur = mapUtilisateurCreateur;
    }

    public Map<String, String> getMapMinistere() {
        return mapMinistere;
    }

    public void setMapMinistere(Map<String, String> mapMinistere) {
        this.mapMinistere = mapMinistere;
    }

    public Map<String, String> getMapDirection() {
        return mapDirection;
    }

    public void setMapDirection(Map<String, String> mapDirection) {
        this.mapDirection = mapDirection;
    }

    public Map<String, String> getMapDestinataire() {
        return mapDestinataire;
    }

    public void setMapDestinataire(Map<String, String> mapDestinataire) {
        this.mapDestinataire = mapDestinataire;
    }
}
