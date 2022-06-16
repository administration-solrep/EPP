package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.ThTemplate;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SSRecherche")
public class SSRecherche extends SolonWebObject {
    protected static final String NOR_KEY_REGEX = "^(?![\\*])[a-zA-Z0-9 \\*;]+$";
    protected static final String NUM_QUESTION_KEY_REGEX = "^(?![\\*])[a-zA-Z0-9 \\*]+$";
    public static final String DATA_URL = "/recherche/rapide?nor=";

    protected ThTemplate buildTemplateRapideSearch(String nor) {
        template.setName("pages/results");
        template.setContext(context);

        String norSession = UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR);

        if (StringUtils.isBlank(norSession) || StringUtils.isNotBlank(nor)) {
            // Dans le cas de la première recherche rapide ou d'une nouvelle recherche rapide
            UserSessionHelper.putUserSessionParameter(context, SSUserSessionKey.NOR, nor);
        } else {
            // Dans le cas d'un retour
            nor = norSession;
        }

        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Recherche rapide : %s", nor),
                DATA_URL + nor,
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        return template;
    }
}
