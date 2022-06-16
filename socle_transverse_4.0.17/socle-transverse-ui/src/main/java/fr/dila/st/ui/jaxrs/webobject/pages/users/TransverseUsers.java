package fr.dila.st.ui.jaxrs.webobject.pages.users;

import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;

import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class TransverseUsers {

    private TransverseUsers() {
        super();
    }

    public static ThTemplate generateListUsersTemplate(
        SpecificContext context,
        ThTemplate template,
        String url,
        String title,
        UsersListForm usersform
    ) {
        template.setContext(context);
        Map<String, Object> map = template.getData();

        context.setNavigationContextTitle(
            new Breadcrumb(title, url, Breadcrumb.TITLE_ORDER, template.getContext().getWebcontext().getRequest())
        );
        context.putInContextData(USERS_LIST_FORM, usersform);
        STUsersList dto = STUIServiceLocator.getSTUtilisateursUIService().getListeUtilisateurs(context);

        map.put(STTemplateConstants.TITLE, title);

        map.put(STTemplateConstants.RESULTAT_LIST, dto.getListe());
        map.put(STTemplateConstants.LST_LETTRES, dto.getLstLettres());

        map.put(STTemplateConstants.LST_COLONNES, dto.getListeColonnes());
        map.put(STTemplateConstants.SEARCH_USER, usersform.getRecherche());
        map.put(STTemplateConstants.DATA_URL, url);
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/users/liste");

        String index = "A";

        if (StringUtils.isNotEmpty(usersform.getIndex())) {
            index = usersform.getIndex().toUpperCase();
        } else if (CollectionUtils.isNotEmpty(dto.getLstLettres())) {
            index = dto.getLstLettres().get(0).toUpperCase();
        }

        map.put(STTemplateConstants.INDEX, index);
        map.put(STTemplateConstants.IS_PAGINATION_VISIBLE, true);
        template.setData(map);

        return template;
    }
}
