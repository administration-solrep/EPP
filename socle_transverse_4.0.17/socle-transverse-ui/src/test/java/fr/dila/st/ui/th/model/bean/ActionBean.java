package fr.dila.st.ui.th.model.bean;

public class ActionBean {
    private Boolean isGranted = false;

    public ActionBean(Boolean granted) {
        isGranted = granted;
    }

    public Boolean getIsGranted() {
        return isGranted;
    }

    public void setIsGranted(Boolean isGranted) {
        this.isGranted = isGranted;
    }
}
