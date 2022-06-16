package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class DossierHistoriqueEPP {
    private List<MessageVersion> lstVersions = new ArrayList<>();

    public List<MessageVersion> getLstVersions() {
        return lstVersions;
    }

    public void setLstVersions(List<MessageVersion> lstVersions) {
        this.lstVersions = lstVersions;
    }
}
