package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.annot.SwBeanInit;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.QueryParam;

@SwBean
public class InitBean extends AbstractInitBean {
    @QueryParam("question")
    private String question;

    @QueryParam("reponse")
    private String reponse;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @SwBeanInit
    public void initQuestion() {
        question = SortOrder.ASC.getValue();
    }

    public void initReponse() {
        question = SortOrder.DESC.getValue();
    }
}
