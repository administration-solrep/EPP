package fr.dila.st.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean
public class OrganigrammeNorDirectionForm {

    public OrganigrammeNorDirectionForm() {}

    @FormParam("nor")
    private String nor;

    @FormParam("idParent")
    private String idParent;

    public String getNor() {
        return nor;
    }

    public void setNor(String nor) {
        this.nor = nor;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }
}
