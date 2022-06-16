package fr.dila.st.ui.th.model;

public class AjaxJSONLayoutThTemplate extends ThTemplate {

    public AjaxJSONLayoutThTemplate() {
        this(null, null);
    }

    public AjaxJSONLayoutThTemplate(String name, SpecificContext context) {
        super(name, context);
        this.setLayout("ajaxJSONLayout");
    }
}
