package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.annot.SwBeanInit;
import fr.dila.st.ui.enums.SortOrder;
import javax.ws.rs.QueryParam;

@SwBean
public class AbstractInitBean {
    @QueryParam("nom")
    private String nom;

    @QueryParam("min")
    private String min;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    @SwBeanInit
    public void initNom() {
        nom = SortOrder.ASC.getValue();
    }

    public void initMin() {
        min = SortOrder.DESC.getValue();
    }
}
