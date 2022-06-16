package fr.dila.ss.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.impl.STUserManagerUIServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collections;
import java.util.Objects;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class SSUserManagerUIServiceImpl extends STUserManagerUIServiceImpl implements STUserManagerUIService {

    @Override
    protected boolean getCanEditUsers(SpecificContext context, boolean allowCurrentUser) {
        if (Boolean.TRUE.equals(getUserManager().areUsersReadOnly())) {
            return false;
        }

        NuxeoPrincipal principal = context.getSession().getPrincipal();
        DocumentModel selectedUser = context.getCurrentDocument();

        if (allowCurrentUser && isSelf(principal, selectedUser)) {
            return true;
        }

        if (principal instanceof SSPrincipal) {
            SSPrincipal pal = (SSPrincipal) principal;
            if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
                return true;
            }

            // Test si l'utilisateur connecté peut modifier les utilisateurs
            // s'ils appartiennent au même ministère
            if (pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_UPDATER)) {
                SSPrincipal sel = (SSPrincipal) getUserManager().getPrincipal(selectedUser.getTitle());
                // si l'utilisateur selectionné est UtilisateurUpdater (admin
                // fonctionnel), non modifiable
                if (sel.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
                    return false;
                }

                if (inSameFonctionalGroup(pal, sel)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean getAllowDeleteUser(SpecificContext context) {
        DocumentModel selectedUser = context.getCurrentDocument();
        Objects.requireNonNull(selectedUser, "User to delete as current document is mandatory");

        NuxeoPrincipal principal = context.getSession().getPrincipal();

        if (Boolean.TRUE.equals(getUserManager().areUsersReadOnly()) || isSelf(principal, selectedUser)) {
            return false;
        }
        if (principal instanceof SSPrincipal) {
            SSPrincipal pal = (SSPrincipal) principal;
            SSPrincipal sel = (SSPrincipal) getUserManager().getPrincipal(selectedUser.getId());

            return (
                (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)) ||
                // Test si l'utilisateur connecté peut effacer les utilisateurs s'ils appartiennent au même ministère
                pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_DELETER) &&
                // si l'utilisateur selectionné est UtilisateurDeleter (admin  fonctionnel), non supprimable
                !sel.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER) &&
                inSameFonctionalGroup(pal, sel)
            );
        }
        return false;
    }

    @Override
    protected boolean inSameFonctionalGroup(NuxeoPrincipal user1, NuxeoPrincipal user2) {
        return !Collections.disjoint(
            ((SSPrincipal) user1).getMinistereIdSet(),
            ((SSPrincipal) user2).getMinistereIdSet()
        );
    }
}
