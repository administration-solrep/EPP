package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = false)
public class WithoutGetterBean {
    @QueryParam("question")
    private SortOrder question = SortOrder.ASC;

    @QueryParam("page")
    private String page = "1";

    //remplace le getter classique
    public SortOrder getMyQuestion() {
        return question;
    }

    public void setQuestion(SortOrder question) {
        this.question = question;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
