package fr.dila.st.core.component;

import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.organigramme.STUsAndDirectionServiceImpl;

public class STUsAndDirectionComponent
    extends ServiceEncapsulateComponent<STUsAndDirectionService, STUsAndDirectionServiceImpl> {

    public STUsAndDirectionComponent() {
        super(STUsAndDirectionService.class, new STUsAndDirectionServiceImpl());
    }
}
