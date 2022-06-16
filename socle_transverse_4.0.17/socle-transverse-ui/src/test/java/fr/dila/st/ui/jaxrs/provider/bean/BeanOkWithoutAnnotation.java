package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.enums.SortOrder;

@SwBean(keepdefaultValue = true)
public class BeanOkWithoutAnnotation {
    private SortOrder questionOrder = SortOrder.ASC;

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
