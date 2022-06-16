package fr.dila.st.core.component;

import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.organigramme.STGouvernementServiceImpl;

public class STGouvernementComponent
    extends ServiceEncapsulateComponent<STGouvernementService, STGouvernementServiceImpl> {

    public STGouvernementComponent() {
        super(STGouvernementService.class, new STGouvernementServiceImpl());
    }
}
