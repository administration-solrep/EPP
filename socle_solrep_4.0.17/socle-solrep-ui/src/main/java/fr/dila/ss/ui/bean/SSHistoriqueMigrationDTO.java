package fr.dila.ss.ui.bean;

import fr.dila.ss.ui.enums.MigrationStatus;
import java.util.Calendar;

public class SSHistoriqueMigrationDTO {

    public SSHistoriqueMigrationDTO() {
        super();
    }

    private String id;

    private String label;

    private MigrationStatus status;

    private String elementFils;

    private String modeleFDR;

    private String posteCreateur;

    private Calendar dateDebut;

    private Calendar dateFin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public MigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MigrationStatus status) {
        this.status = status;
    }

    public String getElementFils() {
        return elementFils;
    }

    public void setElementFils(String elementFils) {
        this.elementFils = elementFils;
    }

    public String getModeleFDR() {
        return modeleFDR;
    }

    public void setModeleFDR(String modeleFDR) {
        this.modeleFDR = modeleFDR;
    }

    public String getPosteCreateur() {
        return posteCreateur;
    }

    public void setPosteCreateur(String posteCreateur) {
        this.posteCreateur = posteCreateur;
    }

    public Calendar getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Calendar dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Calendar getDateFin() {
        return dateFin;
    }

    public void setDateFin(Calendar dateFin) {
        this.dateFin = dateFin;
    }
}
