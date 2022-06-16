package fr.dila.ss.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean
public class ParametreForm {
    @FormParam("valeur")
    private String valeur;

    @FormParam("id")
    private String id;

    public ParametreForm() {}

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
