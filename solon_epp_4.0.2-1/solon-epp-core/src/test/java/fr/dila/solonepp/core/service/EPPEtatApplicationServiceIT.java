package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppPathConstant;
import fr.dila.st.core.service.EtatApplicationServiceIT;
import org.nuxeo.runtime.test.runner.Deploy;

@Deploy("fr.dila.solonepp.core:OSGI-INF/service/epp-etat-application-framework.xml")
public class EPPEtatApplicationServiceIT extends EtatApplicationServiceIT {

    @Override
    protected String getAdminWorkspaceRootId() {
        return "workspace-root";
    }

    @Override
    protected String getEtatApplicationParentPath() {
        return SolonEppPathConstant.ADMIN_WORKSPACE_PATH;
    }
}
