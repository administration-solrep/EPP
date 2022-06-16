package fr.dila.ss.ui.bean;

import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class AlerteForm {

    public AlerteForm() {
        super();
    }

    @NxProp(docType = STAlertConstant.ALERT_DOCUMENT_TYPE, xpath = STSchemaConstant.ECM_UUID_XPATH)
    @FormParam("id")
    private String id;

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_REQUETE_ID
    )
    @FormParam("idRequete")
    private String idRequete;

    @NxProp(docType = STAlertConstant.ALERT_DOCUMENT_TYPE, xpath = STSchemaConstant.DUBLINCORE_TITLE_XPATH)
    @FormParam("titre")
    @SwRequired
    private String titre;

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_DATE_VALIDITY_BEGIN
    )
    @FormParam("datePremiereExecution")
    @SwRequired
    private Calendar datePremiereExecution;

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_DATE_VALIDITY_END
    )
    @FormParam("dateFin")
    @SwRequired
    private Calendar dateFin;

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_PERIODICITY
    )
    @FormParam("frequence")
    @SwRequired
    private String frequence;

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_RECIPIENTS
    )
    @FormParam("destinataires")
    @SwRequired
    private ArrayList<String> destinataires = new ArrayList<>();

    private Map<String, String> mapDestinataires = new HashMap<>();

    @NxProp(
        docType = STAlertConstant.ALERT_DOCUMENT_TYPE,
        xpath = STAlertConstant.ALERT_SHEMA_PREFIX + ":" + STAlertConstant.ALERT_IS_ACTIVATED
    )
    @FormParam("isActivated")
    private Boolean isActivated = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRequete() {
        return idRequete;
    }

    public void setIdRequete(String idRequete) {
        this.idRequete = idRequete;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Calendar getDatePremiereExecution() {
        return datePremiereExecution;
    }

    public void setDatePremiereExecution(Calendar datePremiereExecution) {
        this.datePremiereExecution = datePremiereExecution;
    }

    public Calendar getDateFin() {
        return dateFin;
    }

    public void setDateFin(Calendar dateFin) {
        this.dateFin = dateFin;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public ArrayList<String> getDestinataires() {
        return destinataires;
    }

    public void setDestinataires(ArrayList<String> destinataires) {
        this.destinataires = destinataires;
    }

    public Map<String, String> getMapDestinataires() {
        return mapDestinataires;
    }

    public void setMapDestinataires(Map<String, String> mapDestinataires) {
        this.mapDestinataires = mapDestinataires;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }
}
