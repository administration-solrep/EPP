package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.SSUsersList;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.user.TransverseUser;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "TransverseUser")
public class SSTransverseUser extends TransverseUser {

    @Override
    protected ThTemplate buildMapResultUser(
        STUsersList dto,
        UserSearchForm userSearchForm,
        UsersListForm userListForm
    ) {
        super.buildMapResultUser(dto, userSearchForm, userListForm);

        SSPrincipal principal = (SSPrincipal) context.getSession().getPrincipal();

        if (
            PermissionHelper.isAdminMinisteriel(context.getWebcontext().getPrincipal()) &&
            UserSessionHelper.getUserSessionParameter(context, STUserSessionKey.LIST_USERNAME_FROM_ENTITE) == null
        ) {
            UserSessionHelper.putUserSessionParameter(
                context,
                STUserSessionKey.LIST_USERNAME_FROM_ENTITE,
                STServiceLocator.getOrganigrammeService().getAllUserInSubNode(principal.getMinistereIdSet())
            );
        }

        SSUsersList userList = new SSUsersList();
        Map<String, Object> templateData = template.getData();
        templateData.put(STTemplateConstants.LST_COLONNES, userList.getListeColonnes(userListForm, principal));
        templateData.put(
            SSTemplateConstants.SHOW_DATE_CONNEXION,
            PermissionHelper.isAdminFonctionnel(principal) || PermissionHelper.isAdminMinisteriel(principal)
        );
        template.setData(templateData);
        return template;
    }
}
