package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.SSUsersList;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.jaxrs.webobject.ajax.STRechercheUsersAjax;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheUtilisateur")
public class SSRechercheUsersAjax extends STRechercheUsersAjax {

    @Override
    protected ThTemplate buildMapResultUser(STUsersList dto, UsersListForm formTri) {
        super.buildMapResultUser(dto, formTri);

        SSPrincipal principal = (SSPrincipal) context.getSession().getPrincipal();

        SSUsersList userList = new SSUsersList();
        template.getData().put(STTemplateConstants.LST_COLONNES, userList.getListeColonnes(formTri, principal));
        template
            .getData()
            .put(
                SSTemplateConstants.SHOW_DATE_CONNEXION,
                principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_ALL_USERS_VIEW) ||
                principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_USER_FROM_MINISTERE_VIEW)
            );
        return template;
    }
}
