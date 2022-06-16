package fr.dila.ss.ui.th.model;

import fr.dila.ss.ui.services.AccueilActualitesService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.model.LayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;

@FragmentContainer(
    name = "header",
    value = {
        @Fragment(
            service = AccueilActualitesService.class,
            templateFile = "fragments/headerblocks/accueilActualites",
            template = "accueilActualites",
            order = 4
        )
    }
)
public class SSLayoutThTemplate extends LayoutThTemplate {

    public SSLayoutThTemplate() {
        this(null, null);
    }

    public SSLayoutThTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
