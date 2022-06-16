package fr.dila.st.ui.th.model.templates;

import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;

@Fragment(service = MenuService.class, templateFile = "fragments/menu", template = "menu")
public class FakeSingleFragmentTemplate extends ThTemplate {

    public FakeSingleFragmentTemplate() {
        super();
    }

    public FakeSingleFragmentTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
