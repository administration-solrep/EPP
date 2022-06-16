package fr.dila.st.ui.bean;

public class Onglet {
    private String label;
    private String id;
    private String fragmentFile;
    private String fragmentName;
    private String script;
    private String content = "";
    private Boolean isActif = false;

    public Onglet() {}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFragmentFile() {
        return fragmentFile;
    }

    public void setFragmentFile(String fragment) {
        this.fragmentFile = fragment;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsActif() {
        return isActif;
    }

    public void setIsActif(Boolean isActif) {
        this.isActif = isActif;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
