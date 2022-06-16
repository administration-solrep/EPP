package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.FormParam;

@SwBean
public class ParamFromForm {
    @FormParam("questionOrder")
    private SortOrder questionOrder = SortOrder.ASC;

    @FormParam("page")
    private String page = "1";

    public SortOrder getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(SortOrder questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
