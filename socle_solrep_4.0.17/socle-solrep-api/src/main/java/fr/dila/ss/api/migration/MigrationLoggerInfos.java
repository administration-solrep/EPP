package fr.dila.ss.api.migration;

import fr.dila.ss.api.constant.SSConstant;
import java.io.Serializable;

@SuppressWarnings("serial")
public class MigrationLoggerInfos implements Serializable {
    private long nbStart;

    private long nbCurrent;

    private long nbTotal;

    private String status;

    public MigrationLoggerInfos() {}

    public MigrationLoggerInfos(long nbStart, long nbCurrent, long nbTotal, String status) {
        this.nbStart = nbStart;
        this.nbCurrent = nbCurrent;
        this.nbTotal = nbTotal;
        this.status = status;
    }

    public long getNbStart() {
        return nbStart;
    }

    public void setNbStart(long nbStart) {
        this.nbStart = nbStart;
    }

    public long getNbCurrent() {
        return nbCurrent;
    }

    public void setNbCurrent(long nbCurrent) {
        this.nbCurrent = nbCurrent;
    }

    public long getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(long nbTotal) {
        this.nbTotal = nbTotal;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean enCours() {
        return this.getStatus() != null && this.getStatus().equals(SSConstant.EN_COURS_STATUS);
    }

    public boolean editStatus() {
        return this.enCours() || this.terminee();
    }

    public boolean terminee() {
        return this.getStatus() != null && this.getStatus().equals(SSConstant.TERMINEE_STATUS);
    }
}
