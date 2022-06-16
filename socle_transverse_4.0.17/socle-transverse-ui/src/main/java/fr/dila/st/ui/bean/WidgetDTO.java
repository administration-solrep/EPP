package fr.dila.st.ui.bean;

import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import java.util.ArrayList;
import java.util.List;

public class WidgetDTO extends ChampDescriptor {
    private boolean modifiedInCurVersion;

    // titre du fichier (widget mono file)
    private String pjTitle;

    // name du fichier (widget mono file)
    private String singleFileName;

    // url (widget url)
    private String url;

    private List<String> lstFileNames = new ArrayList<>();

    private List<PieceJointeDTO> lstPieces = new ArrayList<>();

    private boolean pjMultiValue;

    private boolean required;

    public WidgetDTO() {
        super();
    }

    public WidgetDTO(String name, String label) {
        this();
        setName(name);
        setLabel(label);
    }

    public WidgetDTO(String name, String label, String typeChamp) {
        this(name, label);
        setTypeChamp(typeChamp);
    }

    public boolean isModifiedInCurVersion() {
        return modifiedInCurVersion;
    }

    public void setModifiedInCurVersion(boolean modifiedInCurVersion) {
        this.modifiedInCurVersion = modifiedInCurVersion;
    }

    public String getPjTitle() {
        return pjTitle;
    }

    public void setPjTitle(String pjTitle) {
        this.pjTitle = pjTitle;
    }

    public String getSingleFileName() {
        return singleFileName;
    }

    public void setSingleFileName(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getLstFileNames() {
        return lstFileNames;
    }

    public void setLstFileNames(List<String> lstFiles) {
        this.lstFileNames = lstFiles;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<PieceJointeDTO> getLstPieces() {
        return lstPieces;
    }

    public void setLstPieces(List<PieceJointeDTO> lstPieces) {
        this.lstPieces = lstPieces;
    }

    public boolean isPjMultiValue() {
        return pjMultiValue;
    }

    public void setPjMultiValue(boolean pjMultiValue) {
        this.pjMultiValue = pjMultiValue;
    }

    public Object getValueParamByNameOrNull(String name) {
        return getParametres()
            .stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .map(Parametre::getValue)
            .orElse(null);
    }
}
