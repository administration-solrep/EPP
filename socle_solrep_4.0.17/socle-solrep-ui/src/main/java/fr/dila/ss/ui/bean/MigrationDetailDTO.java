package fr.dila.ss.ui.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dila.st.ui.helper.MapHelper;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class MigrationDetailDTO {

    public MigrationDetailDTO() {}

    public MigrationDetailDTO(String migrationType) {
        this.migrationType = migrationType;
    }

    private String migrationId;

    private String migrationType;

    private String oldElement = StringUtils.EMPTY;

    private String newElement = StringUtils.EMPTY;

    @JsonProperty("oldElement-key")
    private String oldElementId = StringUtils.EMPTY;

    @JsonProperty("newElement-key")
    private String newElementId = StringUtils.EMPTY;

    private String oldMinistere = StringUtils.EMPTY;

    private String newMinistere = StringUtils.EMPTY;

    @JsonProperty("oldMinistere-key")
    private String oldMinistereId = StringUtils.EMPTY;

    @JsonProperty("newMinistere-key")
    private String newMinistereId = StringUtils.EMPTY;

    private Boolean deleteOld = false;

    private Boolean migrerModelesFdr = true;

    private Boolean migrationWithDossierClos = true;

    private String errorMessage;

    private String status;

    @JsonIgnore
    private Map<String, MigrationProgressDTO> actionProgress = new LinkedHashMap<>();

    @JsonIgnore
    private Map<String, String> mapOldElement = new HashMap<>();

    @JsonIgnore
    private Map<String, String> mapNewElement = new HashMap<>();

    @JsonIgnore
    private Map<String, String> mapOldMinistere = new HashMap<>();

    @JsonIgnore
    private Map<String, String> mapNewMinistere = new HashMap<>();

    public String getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    public String getMigrationType() {
        return migrationType;
    }

    public void setMigrationType(String migrationType) {
        this.migrationType = migrationType;
    }

    public String getOldElement() {
        return oldElement;
    }

    public void setOldElement(String oldElement) {
        this.oldElement = oldElement;
    }

    public String getNewElement() {
        return newElement;
    }

    public void setNewElement(String newElement) {
        this.newElement = newElement;
    }

    public String getOldElementId() {
        return oldElementId;
    }

    public void setOldElementId(String oldElementId) {
        this.oldElementId = oldElementId;
    }

    public String getNewElementId() {
        return newElementId;
    }

    public void setNewElementId(String newElementId) {
        this.newElementId = newElementId;
    }

    public String getOldMinistere() {
        return oldMinistere;
    }

    public void setOldMinistere(String oldMinistere) {
        this.oldMinistere = oldMinistere;
    }

    public String getNewMinistere() {
        return newMinistere;
    }

    public void setNewMinistere(String newMinistere) {
        this.newMinistere = newMinistere;
    }

    public String getOldMinistereId() {
        return oldMinistereId;
    }

    public void setOldMinistereId(String oldMinistereId) {
        this.oldMinistereId = oldMinistereId;
    }

    public String getNewMinistereId() {
        return newMinistereId;
    }

    public void setNewMinistereId(String newMinistereId) {
        this.newMinistereId = newMinistereId;
    }

    public Boolean getDeleteOld() {
        return deleteOld;
    }

    public void setDeleteOld(Boolean deleteOld) {
        this.deleteOld = deleteOld;
    }

    public Boolean getMigrerModelesFdr() {
        return migrerModelesFdr;
    }

    public void setMigrerModelesFdr(Boolean migrerModelesFdr) {
        this.migrerModelesFdr = migrerModelesFdr;
    }

    public Boolean getMigrationWithDossierClos() {
        return migrationWithDossierClos;
    }

    public void setMigrationWithDossierClos(Boolean migrationWithDossierClos) {
        this.migrationWithDossierClos = migrationWithDossierClos;
    }

    public Map<String, MigrationProgressDTO> getActionProgress() {
        return actionProgress;
    }

    public void setActionProgress(Map<String, MigrationProgressDTO> actionProgress) {
        this.actionProgress = actionProgress;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getMapOldElement() {
        return MapHelper.copy(mapOldElement);
    }

    public void setMapOldElement(Map<String, String> mapOldElement) {
        this.mapOldElement = MapHelper.copy(mapOldElement);
    }

    public Map<String, String> getMapNewElement() {
        return MapHelper.copy(mapNewElement);
    }

    public void setMapNewElement(Map<String, String> mapNewElement) {
        this.mapNewElement = MapHelper.copy(mapNewElement);
    }

    public Map<String, String> getMapOldMinistere() {
        return MapHelper.copy(mapOldMinistere);
    }

    public void setMapOldMinistere(Map<String, String> mapOldMinistere) {
        this.mapOldMinistere = MapHelper.copy(mapOldMinistere);
    }

    public Map<String, String> getMapNewMinistere() {
        return MapHelper.copy(mapNewMinistere);
    }

    public void setMapNewMinistere(Map<String, String> mapNewMinistere) {
        this.mapNewMinistere = MapHelper.copy(mapNewMinistere);
    }
}
