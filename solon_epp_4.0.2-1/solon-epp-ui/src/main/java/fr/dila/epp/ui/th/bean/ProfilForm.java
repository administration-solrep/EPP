package fr.dila.epp.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean
public class ProfilForm {
    @FormParam("notif")
    private Boolean mailNotification;

    public ProfilForm() {}

    public Boolean getMailNotification() {
        return mailNotification;
    }

    public void setMailNotification(Boolean mailNotification) {
        this.mailNotification = mailNotification;
    }
}
