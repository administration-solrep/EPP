package fr.dila.ss.ui.bean;

import java.util.Calendar;

public class SSHistoriqueMigrationDetailLigneDTO {

    public SSHistoriqueMigrationDetailLigneDTO() {
        super();
    }

    private String message;

    private Calendar dateDebut;

    private Calendar dateFin;

    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
