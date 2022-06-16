package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.impl.EppUserManagerUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STUserManagerUIService;

public class EppUserManagerUIComponent
    extends ServiceEncapsulateComponent<STUserManagerUIService, EppUserManagerUIServiceImpl> {

    /**
     * Default constructor
     */
    public EppUserManagerUIComponent() {
        super(STUserManagerUIService.class, new EppUserManagerUIServiceImpl());
    }
}
