package fr.dila.ss.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.PaginationForm;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class BirtReportListForm extends PaginationForm {
    @QueryParam("titre")
    private SortOrder titre;

    public BirtReportListForm() {
        super(SIZE_20);
        titre = SortOrder.ASC;
    }

    public SortOrder getTitre() {
        return titre;
    }

    public void setTitre(SortOrder titre) {
        this.titre = titre;
    }
}
