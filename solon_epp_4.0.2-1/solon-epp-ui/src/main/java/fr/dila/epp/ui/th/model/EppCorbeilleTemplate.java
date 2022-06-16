package fr.dila.epp.ui.th.model;

import fr.dila.epp.ui.services.EppCorbeilleMenuService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "main_corbeille", category = "MAIN_MENU") },
    value = @FragmentContainer(
        name = "left",
        value = {
            @Fragment(
                service = EppCorbeilleMenuService.class,
                templateFile = "fragments/leftblocks/corbeilleMenu",
                template = "leftMenu",
                order = 1
            )
        }
    )
)
public class EppCorbeilleTemplate extends EppLayoutThTemplate {

    public EppCorbeilleTemplate() {
        this(null, null);
    }

    public EppCorbeilleTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
