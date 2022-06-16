package fr.dila.epp.ui.services.impl;

import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.impl.STUserManagerUIServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collections;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class EppUserManagerUIServiceImpl extends STUserManagerUIServiceImpl {

    @Override
    public boolean getAllowEditUser(SpecificContext context) {
        boolean allowed = super.getAllowEditUser(context);

        //Si on n'est pas super Administrateur  ou soit même on vérifie qu'on a le droit d'édition des utilisateurs et qu'on est dans son instituion
        if (!allowed) {
            DocumentModel selectedUserDoc = context.getCurrentDocument();
            EppPrincipal selectedUser = (EppPrincipal) STServiceLocator
                .getUserManager()
                .getPrincipal(selectedUserDoc.getId());
            EppPrincipal currentUser = (EppPrincipal) context.getSession().getPrincipal();
            allowed =
                currentUser.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER) &&
                inSameFonctionalGroup(selectedUser, currentUser);
        }

        return allowed;
    }

    @Override
    protected boolean inSameFonctionalGroup(NuxeoPrincipal user1, NuxeoPrincipal user2) {
        return !Collections.disjoint(
            ((EppPrincipal) user1).getInstitutionIdSet(),
            ((EppPrincipal) user2).getInstitutionIdSet()
        );
    }
}
