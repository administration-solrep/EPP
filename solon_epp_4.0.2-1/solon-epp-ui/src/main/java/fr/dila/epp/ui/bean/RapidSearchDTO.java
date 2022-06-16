package fr.dila.epp.ui.bean;

import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.ArrayList;
import java.util.List;

public class RapidSearchDTO {
    private List<SelectValueDTO> lstTypeCommunication = new ArrayList<>();
    private List<SelectValueDTO> lstInstitution = new ArrayList<>();

    public List<SelectValueDTO> getLstTypeCommunication() {
        return lstTypeCommunication;
    }

    public void setLstTypeCommunication(List<SelectValueDTO> lstTypeCommunication) {
        this.lstTypeCommunication = lstTypeCommunication;
    }

    public List<SelectValueDTO> getLstInstitution() {
        return lstInstitution;
    }

    public void setLstInstitution(List<SelectValueDTO> lstInstitution) {
        this.lstInstitution = lstInstitution;
    }
}
