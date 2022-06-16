package fr.dila.st.ui.th.model.templates;

import fr.dila.st.ui.services.HeaderService;
import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.model.FakeAnnot;
import fr.dila.st.ui.th.model.ThTemplate;

@FakeAnnot("fake")
@FragmentContainer(
    name = "header",
    value = {
        @Fragment(service = HeaderService.class, templateFile = "fragments/header", template = "header", order = 1)
    }
)
@ActionMenu(id = "test", category = "MAIN_MENU")
@FragmentContainer(
    name = "menu",
    value = { @Fragment(service = MenuService.class, templateFile = "fragments/menu", template = "menu", order = 1) }
)
public class FakeMultipleComplexFragmentTemplate extends ThTemplate {}
