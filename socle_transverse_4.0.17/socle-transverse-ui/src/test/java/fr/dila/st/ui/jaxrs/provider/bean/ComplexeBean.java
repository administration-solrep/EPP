package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class ComplexeBean {
    @FormParam("listComplexe")
    private ArrayList<TestBean> liste = new ArrayList<>();

    @FormParam("beanComplexe")
    private TestBean bean;

    public ArrayList<TestBean> getListe() {
        return liste;
    }

    public void setListe(ArrayList<TestBean> liste) {
        this.liste = liste;
    }

    public TestBean getBean() {
        return bean;
    }

    public void setBean(TestBean bean) {
        this.bean = bean;
    }
}
