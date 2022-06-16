package fr.dila.epp.ui.services.actions.component;

import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.impl.CorbeilleActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class CorbeilleActionComponent
    extends ServiceEncapsulateComponent<CorbeilleActionService, CorbeilleActionServiceImpl> {

    /**
     * Default constructor
     */
    public CorbeilleActionComponent() {
        super(CorbeilleActionService.class, new CorbeilleActionServiceImpl());
    }
}
