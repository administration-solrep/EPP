package fr.dila.st.ui.th.model;

public class DisconnectedLayoutThTemplate extends ThTemplate {

    public DisconnectedLayoutThTemplate() {
        this(null, null);
    }

    public DisconnectedLayoutThTemplate(String name, SpecificContext context) {
        super(name, context);
        this.setLayout("pageLayout");
    }
}
