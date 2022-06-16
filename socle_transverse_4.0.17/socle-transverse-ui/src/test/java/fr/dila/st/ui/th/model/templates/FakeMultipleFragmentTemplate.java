package fr.dila.st.ui.th.model.templates;

import fr.dila.st.ui.services.HeaderService;
import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.model.FakeAnnot;
import fr.dila.st.ui.th.model.ThTemplate;

@FakeAnnot("fake")
@Fragment(service = HeaderService.class, templateFile = "fragments/header", template = "header", order = 1)
@Fragment(service = MenuService.class, templateFile = "fragments/menu", template = "menu", order = 1)
public class FakeMultipleFragmentTemplate extends ThTemplate {}
