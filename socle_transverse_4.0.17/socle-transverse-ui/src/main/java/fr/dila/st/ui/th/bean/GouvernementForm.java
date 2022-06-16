package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class GouvernementForm {

    public GouvernementForm() {
        super();
    }

    @FormParam("id")
    private String id;

    @FormParam("appellation")
    private String appellation;

    @FormParam("dateDebut")
    private String dateDebut = SolonDateConverter.DATE_SLASH.formatNow();

    @FormParam("dateFin")
    private String dateFin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
}
