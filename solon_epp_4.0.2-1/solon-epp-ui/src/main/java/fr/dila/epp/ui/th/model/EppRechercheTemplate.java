package fr.dila.epp.ui.th.model;

import fr.dila.epp.ui.services.RechercheMenuService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_RECHERCHE", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = RechercheMenuService.class,
                    templateFile = "fragments/leftblocks/rechercheMenu",
                    template = "rechercheMenu",
                    order = 1
                )
            }
        )
    }
)
public class EppRechercheTemplate extends EppLayoutThTemplate {

    public EppRechercheTemplate() {
        this(null, null);
    }

    public EppRechercheTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
