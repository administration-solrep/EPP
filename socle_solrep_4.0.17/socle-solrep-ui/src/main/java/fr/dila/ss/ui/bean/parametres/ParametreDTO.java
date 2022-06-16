package fr.dila.ss.ui.bean.parametres;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;

public class ParametreDTO {
    @NxProp(xpath = STSchemaConstant.ECM_UUID_XPATH, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String id;

    @NxProp(xpath = STSchemaConstant.ECM_NAME_XPATH, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String name;

    @NxProp(xpath = STSchemaConstant.DUBLINCORE_TITLE_XPATH, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String titre;

    @NxProp(xpath = STSchemaConstant.PARAMETRE_VALUE, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String valeur;

    @NxProp(xpath = STSchemaConstant.PARAMETRE_UNIT, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String unite;

    @NxProp(xpath = STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String description;

    @NxProp(xpath = STSchemaConstant.PARAMETRE_TYPE, docType = STConstant.PARAMETRE_DOCUMENT_TYPE)
    private String type;

    public ParametreDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
