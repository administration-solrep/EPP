package fr.dila.st.ui.bean;

/**
 * Utiliser pour le rendu de l'état d'une étape de feuille de route : contient l'image à afficher le label associé, un
 * style eventuel et un texte affiché à coté de l'image
 *
 *
 * <image value="{img}" alt={label} title={label} {style}> {text}
 *
 * @author SPL
 *
 */
public class RouteElementStateRenderDTO {
    private String img;
    private String label;
    private String text;
    private String style;

    public RouteElementStateRenderDTO(String img, String label, String text, String style) {
        this.img = img;
        this.label = label;
        this.text = text;
        this.style = style;
    }

    public RouteElementStateRenderDTO(String img, String label) {
        this(img, label, "", "");
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
