package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppPathConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.service.EtatApplicationServiceImpl;

public class EPPEtatApplicationServiceImpl extends EtatApplicationServiceImpl implements EtatApplicationService {

    @Override
    protected String getEtatApplicationParentPath() {
        return SolonEppPathConstant.ADMIN_WORKSPACE_PATH;
    }
}
