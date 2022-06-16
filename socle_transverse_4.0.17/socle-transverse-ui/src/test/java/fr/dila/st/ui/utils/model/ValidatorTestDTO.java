package fr.dila.st.ui.utils.model;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwLength;
import fr.dila.st.ui.validators.annot.SwListValues;
import fr.dila.st.ui.validators.annot.SwNotEmpty;
import javax.ws.rs.FormParam;

@SwBean
public class ValidatorTestDTO {
    @SwNotEmpty
    @FormParam("idBranch")
    private String idBranche;

    private String typeAjout;

    @FormParam("typeCreation")
    private String typeCreation;

    @FormParam("typeRef")
    private String typeRef;

    @SwListValues({ "20", "25", "30" })
    private String listValues;

    @SwLength(min = 5, max = 10)
    private String length;

    public String getIdBranche() {
        return idBranche;
    }

    public void setIdBranche(String idBranche) {
        this.idBranche = idBranche;
    }

    public String getTypeAjout() {
        return typeAjout;
    }

    public void setTypeAjout(String typeAjout) {
        this.typeAjout = typeAjout;
    }

    public String getTypeCreation() {
        return typeCreation;
    }

    public void setTypeCreation(String typeCreation) {
        this.typeCreation = typeCreation;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public String getListValues() {
        return listValues;
    }

    public void setListValues(String listValues) {
        this.listValues = listValues;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
