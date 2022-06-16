package fr.dila.st.ui.th.model;

import fr.dila.st.ui.services.BreadcrumbsService;
import fr.dila.st.ui.services.FooterService;
import fr.dila.st.ui.services.HeaderService;
import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.services.PasswordService;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;

@FragmentContainer(
    name = "header",
    value = {
        @Fragment(
            service = MenuService.class,
            templateFile = "fragments/headerblocks/headerLeft",
            template = "headerLeft",
            order = 1
        ),
        @Fragment(
            service = HeaderService.class,
            templateFile = "fragments/headerblocks/headerRight",
            template = "headerRight",
            order = 2
        ),
        @Fragment(
            service = PasswordService.class,
            templateFile = "fragments/headerblocks/resetPassword",
            template = "resetPassword",
            order = 3
        )
    }
)
@FragmentContainer(
    name = "breadcrumbs",
    value = {
        @Fragment(
            service = BreadcrumbsService.class,
            templateFile = "fragments/breadcrumbsblocks/breadcrumbsLeft",
            template = "breadcrumbsLeft",
            order = 1
        )
    }
)
@FragmentContainer(
    name = "footer",
    value = {
        @Fragment(
            service = FooterService.class,
            templateFile = "fragments/footerblocks/footerInfo",
            template = "footerInfo",
            order = 1
        )
    }
)
public class LayoutThTemplate extends ThTemplate {

    public LayoutThTemplate() {
        this(null, null);
    }

    public LayoutThTemplate(String name, SpecificContext context) {
        super(name, context);
        this.setLayout("pageLayout");
    }
}
