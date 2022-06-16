package fr.dila.ss.ui.bean;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.util.List;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class SSUsersList extends STUsersList {

    public List<ColonneInfo> getListeColonnes(UsersListForm form, NuxeoPrincipal principal) {
        buildColonnes(form, principal);
        return listeColonnes;
    }

    private void buildColonnes(UsersListForm form, NuxeoPrincipal principal) {
        buildColonnes(form);
        if (form != null && haveSortableColumns) {
            if (
                principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_ALL_USERS_VIEW) ||
                principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_USER_FROM_MINISTERE_VIEW)
            ) {
                listeColonnes.add(
                    new ColonneInfo(
                        "users.column.header.dateDerniereConnexion",
                        true,
                        UsersListForm.DATE_LAST_CONNEXION_TRI,
                        form.getDateLastConnexion()
                    )
                );
            }
        } else {
            if (PermissionHelper.isAdminFonctionnel(principal) || PermissionHelper.isAdminMinisteriel(principal)) {
                listeColonnes.add(
                    new ColonneInfo("users.column.header.dateDerniereConnexion", false, true, false, true)
                );
            }
        }
    }
}
