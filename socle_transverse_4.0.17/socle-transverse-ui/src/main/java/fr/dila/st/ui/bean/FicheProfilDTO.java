package fr.dila.st.ui.bean;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.mapper.MapDoc2BeanListFunctionProcess;
import java.util.ArrayList;
import java.util.List;

public class FicheProfilDTO {
    @NxProp(docType = STSchemaConstant.GROUP_SCHEMA, xpath = STSchemaConstant.GROUP_XPATH_ID)
    private String id;

    @NxProp(docType = STSchemaConstant.GROUP_SCHEMA, xpath = STSchemaConstant.GROUP_XPATH_LABEL)
    private String label;

    @NxProp(
        docType = STSchemaConstant.GROUP_SCHEMA,
        xpath = STSchemaConstant.GROUP_XPATH_FUNCTIONS,
        process = MapDoc2BeanListFunctionProcess.class
    )
    private List<SelectValueDTO> fonctions = new ArrayList<>();

    private List<ColonneInfo> lstColonnes = new ArrayList<>();

    public FicheProfilDTO() {
        super();
    }

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

    public List<SelectValueDTO> getFonctions() {
        return fonctions;
    }

    public void setFonctions(List<SelectValueDTO> fonctions) {
        this.fonctions = fonctions;
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public void setLstColonnes(List<ColonneInfo> lstColonnes) {
        this.lstColonnes = lstColonnes;
    }
}
