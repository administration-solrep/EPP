package fr.dila.st.core.component;

import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.EtatApplicationServiceImpl;

public class EtatApplicationComponent
    extends ServiceEncapsulateComponent<EtatApplicationService, EtatApplicationServiceImpl> {

    public EtatApplicationComponent() {
        super(EtatApplicationService.class, new EtatApplicationServiceImpl());
    }
}
