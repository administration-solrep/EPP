package fr.dila.ss.ui.bean;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class MigrationDTO {

    public MigrationDTO() {}

    @FormParam("details[]")
    private ArrayList<MigrationDetailDTO> details = new ArrayList<>();

    @FormParam("status")
    private String status;

    public ArrayList<MigrationDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<MigrationDetailDTO> details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean enCours() {
        return SSConstant.EN_COURS_STATUS.equals(this.getStatus());
    }

    public boolean isRunning() {
        return this.enCours() || this.terminee();
    }

    public boolean terminee() {
        return SSConstant.TERMINEE_STATUS.equals(this.getStatus());
    }
}
