package fr.dila.epp.ui.services.actions.impl;

import fr.dila.epp.ui.services.actions.OrganigrammeActionService;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import org.nuxeo.ecm.core.api.CoreSession;

public class OrganigrammeActionServiceImpl implements OrganigrammeActionService {

    @Override
    public boolean isUserAllowedToEditOrganigramme(CoreSession session, String institutionId) {
        EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();

        return (
            eppPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_UPDATER) &&
            eppPrincipal.getInstitutionIdSet().contains(institutionId)
        );
    }
}
