package fr.dila.st.ui.th.model.templates;

import fr.dila.st.ui.services.FragmentService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.model.FakeAnnot;

@FakeAnnot("fake")
@FragmentContainer(
    name = "header",
    value = {
        @Fragment(service = FragmentService.class, templateFile = "fragments/header", template = "header", order = 1)
    }
)
public class FakeOverrideFragmentTemplate extends FakeMultipleComplexFragmentTemplate {}
