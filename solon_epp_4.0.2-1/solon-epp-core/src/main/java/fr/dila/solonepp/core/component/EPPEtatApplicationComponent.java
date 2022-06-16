package fr.dila.solonepp.core.component;

import fr.dila.solonepp.core.service.EPPEtatApplicationServiceImpl;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EPPEtatApplicationComponent
    extends ServiceEncapsulateComponent<EtatApplicationService, EPPEtatApplicationServiceImpl> {

    public EPPEtatApplicationComponent() {
        super(EtatApplicationService.class, new EPPEtatApplicationServiceImpl());
    }
}
