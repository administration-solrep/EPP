package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.QueryParam;

@SwBean
public class WithoutConstructorBean {
    @QueryParam("question")
    private SortOrder question = SortOrder.ASC;

    @QueryParam("page")
    private String page = "1";

    //On définit un constructeur pécifique sans de constructeur générique
    //Le provider ne gère pas de constructeur spécifique
    public WithoutConstructorBean(String page) {
        this.page = page;
    }

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
