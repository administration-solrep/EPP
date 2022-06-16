package fr.dila.st.ui.bean.actions;

public class DossierLockActionDTO {
    private boolean canLockCurrentDossier;

    public DossierLockActionDTO() {}

    public boolean getCanLockCurrentDossier() {
        return canLockCurrentDossier;
    }

    public void setCanLockCurrentDossier(boolean canLockCurrentDossier) {
        this.canLockCurrentDossier = canLockCurrentDossier;
    }
}
