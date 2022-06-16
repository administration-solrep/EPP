package fr.dila.ss.ui.th.bean;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class FicheProfilForm {
    @FormParam("id")
    @NxProp(docType = STSchemaConstant.GROUP_SCHEMA, xpath = STSchemaConstant.GROUP_XPATH_ID)
    private String id;

    @FormParam("label")
    @NxProp(docType = STSchemaConstant.GROUP_SCHEMA, xpath = STSchemaConstant.GROUP_XPATH_LABEL)
    private String label;

    @FormParam("fonctions[]")
    @NxProp(docType = STSchemaConstant.GROUP_SCHEMA, xpath = STSchemaConstant.GROUP_XPATH_FUNCTIONS)
    private ArrayList<String> fonctionsId;

    public FicheProfilForm() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<String> getFonctionsId() {
        return fonctionsId;
    }

    public void setFonctionsId(ArrayList<String> fonctionsId) {
        this.fonctionsId = fonctionsId;
    }
}
