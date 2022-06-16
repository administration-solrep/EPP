package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.impl.EvenementUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EvenementUIComponent extends ServiceEncapsulateComponent<EvenementUIService, EvenementUIServiceImpl> {

    /**
     * Default constructor
     */
    public EvenementUIComponent() {
        super(EvenementUIService.class, new EvenementUIServiceImpl());
    }
}
