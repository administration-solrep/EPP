package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class SimpleWithDefaultFromQuery {
    @QueryParam("question")
    private String question = "test question";

    @QueryParam("questionOrder")
    private SortOrder questionOrder = SortOrder.ASC;

    @QueryParam("page")
    private String page = "1";

    @QueryParam("date")
    private Calendar date = new GregorianCalendar(2020, 11, 12);

    @QueryParam("valid")
    private Boolean valid = true;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

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

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
