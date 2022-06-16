package fr.dila.ss.ui.bean;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SSConsultDossierDTO {
    @NxProp(xpath = STSchemaConstant.ECM_UUID_XPATH, docType = STConstant.DOSSIER_DOCUMENT_TYPE)
    private String id;

    private boolean isVerrouille;

    private String lockTime = "";

    private String lockOwner = "";

    private List<String> nextStepLabel = new ArrayList<>();

    private List<String> actualStepLabel = new ArrayList<>();

    private Date dateLastStep;

    private boolean isDone = false;

    public SSConsultDossierDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsVerrouille() {
        return isVerrouille;
    }

    public void setIsVerrouille(boolean verrouille) {
        isVerrouille = verrouille;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public List<String> getNextStepLabel() {
        return nextStepLabel;
    }

    public void setNextStepLabel(List<String> nextStepLabel) {
        this.nextStepLabel = nextStepLabel;
    }

    public List<String> getActualStepLabel() {
        return actualStepLabel;
    }

    public void setActualStepLabel(List<String> actualStepLabel) {
        this.actualStepLabel = actualStepLabel;
    }

    public Date getDateLastStep() {
        return dateLastStep;
    }

    public void setDateLastStep(Date dateLastStep) {
        this.dateLastStep = dateLastStep;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
