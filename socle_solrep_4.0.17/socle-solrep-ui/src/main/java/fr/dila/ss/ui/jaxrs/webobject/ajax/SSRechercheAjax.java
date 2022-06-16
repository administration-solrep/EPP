package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.ajax.STRechercheAjax;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheAjax")
public class SSRechercheAjax extends STRechercheAjax {
    public static final String FDR_KEY = "intituleFdr";
    public static final String RAPID_SEARCH_KEY = "rapidSearchKey";

    public SSRechercheAjax() {
        super();
        this.suggestionFunctions.put(
                FDR_KEY,
                input -> SSUIServiceLocator.getFeuilleRouteSuggestionProviderService().getSuggestions(input, context)
            );
    }

    protected ThTemplate buildTemplateRapideSearch(AbstractSortablePaginationForm form) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/tableDossiers");
        template.setContext(context);

        String nor = UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR);

        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Consultation du dossier %s", nor),
                "/recherche/rapide",
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        UserSessionHelper.putUserSessionParameter(context, SSUserSessionKey.SEARCH_RESULT_FORM, form);

        return template;
    }
}
