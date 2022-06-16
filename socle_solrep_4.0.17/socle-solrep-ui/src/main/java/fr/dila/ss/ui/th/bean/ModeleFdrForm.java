package fr.dila.ss.ui.th.bean;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean
public class ModeleFdrForm {
    @FormParam("id")
    private String id;

    @NxProp(docType = SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, xpath = STSchemaConstant.DUBLINCORE_TITLE_XPATH)
    @FormParam("intitule")
    private String intitule;

    @NxProp(
        docType = SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_XPATH
    )
    @FormParam("ministere-key")
    private String idMinistere;

    @FormParam("ministere")
    private String libelleMinistere;

    @NxProp(docType = SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, xpath = SSFeuilleRouteConstant.FEUILLE_ROUTE_DEFAUT_XPATH)
    @FormParam("modeleParDefaut")
    private boolean modeleParDefaut;

    @NxProp(docType = SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, xpath = STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY)
    @FormParam("description")
    private String description;

    @FormParam("direction-key")
    private String idDirection;

    @FormParam("direction")
    private String libelleDirection;

    private Map<String, String> mapMinistere;

    private String etat;

    private Boolean isLock;

    private Boolean isLockByCurrentUser;

    private FdrDTO fdrDto;

    private Map<String, String> mapDirection;

    public ModeleFdrForm() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getIdMinistere() {
        return idMinistere;
    }

    public void setIdMinistere(String idMinistere) {
        this.idMinistere = idMinistere;
    }

    public String getLibelleMinistere() {
        return libelleMinistere;
    }

    public void setLibelleMinistere(String libelleMinistere) {
        this.libelleMinistere = libelleMinistere;
    }

    public Boolean getModeleParDefaut() {
        return modeleParDefaut;
    }

    public void setModeleParDefaut(Boolean modeleParDefaut) {
        this.modeleParDefaut = modeleParDefaut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdDirection() {
        return idDirection;
    }

    public void setIdDirection(String idDirection) {
        this.idDirection = idDirection;
    }

    public String getLibelleDirection() {
        return libelleDirection;
    }

    public void setLibelleDirection(String libelleDirection) {
        this.libelleDirection = libelleDirection;
    }

    public Boolean getIsLock() {
        return isLock;
    }

    public void setIsLock(Boolean isLock) {
        this.isLock = isLock;
    }

    public Boolean getIsLockByCurrentUser() {
        return isLockByCurrentUser;
    }

    public void setIsLockByCurrentUser(Boolean isLockByCurrentUser) {
        this.isLockByCurrentUser = isLockByCurrentUser;
    }

    public FdrDTO getFdrDto() {
        return fdrDto;
    }

    public void setFdrDto(FdrDTO fdrDto) {
        this.fdrDto = fdrDto;
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
}
