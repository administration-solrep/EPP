package fr.dila.epp.ui.services.actions.component;

import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.epp.ui.services.actions.impl.EvenementTypeActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EvenementTypeActionComponent
    extends ServiceEncapsulateComponent<EvenementTypeActionService, EvenementTypeActionServiceImpl> {

    /**
     * Default constructor
     */
    public EvenementTypeActionComponent() {
        super(EvenementTypeActionService.class, new EvenementTypeActionServiceImpl());
    }
}
