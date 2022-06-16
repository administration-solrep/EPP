package fr.dila.st.ui.bean;

public class DropDownItem {
    private String label;
    private String icon;
    private String url;

    public DropDownItem(String url, String label) {
        this(url, label, null);
    }

    public DropDownItem(String url, String label, String icon) {
        this.url = url;
        this.icon = icon;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
