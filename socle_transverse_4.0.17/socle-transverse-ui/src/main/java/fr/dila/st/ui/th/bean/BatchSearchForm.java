package fr.dila.st.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class BatchSearchForm extends PaginationForm {
    @QueryParam("dateBatchDebut")
    @FormParam("dateBatchDebut")
    private String debut;

    @QueryParam("dateBatchFin")
    @FormParam("dateBatchFin")
    private String fin;

    public BatchSearchForm() {
        super();
        debut = SolonDateConverter.DATE_SLASH.formatNow();
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }
}
