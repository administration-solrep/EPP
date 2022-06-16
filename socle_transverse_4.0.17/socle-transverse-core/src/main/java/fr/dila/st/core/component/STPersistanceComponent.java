package fr.dila.st.core.component;

import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STPersistanceServiceImpl;

public class STPersistanceComponent
    extends ServiceEncapsulateComponent<STPersistanceService, STPersistanceServiceImpl> {

    public STPersistanceComponent() {
        super(STPersistanceService.class, new STPersistanceServiceImpl());
    }
}
