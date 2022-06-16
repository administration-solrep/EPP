package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean
public class MultipleAnnotationForm {
    @FormParam("questionForm")
    @QueryParam("questionQuery")
    private SortOrder question = SortOrder.ASC;

    @FormParam("pageForm")
    @QueryParam("pageQuery")
    private String page = "1";

    public SortOrder getQuestion() {
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
