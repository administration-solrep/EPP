package fr.dila.st.ui.th.model.templates;

import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.model.SpecificContext;

@Fragment(service = MenuService.class, templateFile = "fragments/menu", template = "menu")
public class FakeTwiceSingleFragmentTemplate extends FakeSingleFragmentTemplate {

    public FakeTwiceSingleFragmentTemplate() {
        super();
    }

    public FakeTwiceSingleFragmentTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
