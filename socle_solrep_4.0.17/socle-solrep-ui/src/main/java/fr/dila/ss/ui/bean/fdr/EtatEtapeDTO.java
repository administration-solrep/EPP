package fr.dila.ss.ui.bean.fdr;

public class EtatEtapeDTO {
    private String label;
    private String icon;

    public EtatEtapeDTO() {
        super();
    }

    public EtatEtapeDTO(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
