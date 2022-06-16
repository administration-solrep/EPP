package fr.dila.ss.ui.th.model;

import fr.dila.st.ui.services.AdminMenuService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_ADMIN", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = AdminMenuService.class,
                    templateFile = "fragments/leftblocks/leftMenu",
                    template = "leftMenu",
                    order = 1
                )
            }
        )
    }
)
public class SSAdminTemplate extends SSLayoutThTemplate {

    public SSAdminTemplate() {
        this(null, null);
    }

    public SSAdminTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
