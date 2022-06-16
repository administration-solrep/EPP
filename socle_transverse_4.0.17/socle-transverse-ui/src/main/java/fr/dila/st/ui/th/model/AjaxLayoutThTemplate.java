package fr.dila.st.ui.th.model;

public class AjaxLayoutThTemplate extends ThTemplate {

    public AjaxLayoutThTemplate() {
        this(null, null);
    }

    public AjaxLayoutThTemplate(String name, SpecificContext context) {
        super(name, context);
        this.setLayout("ajaxLayout");
    }
}
