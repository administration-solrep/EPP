package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.epp.ui.services.impl.SelectValueUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SelectValueUIComponent
    extends ServiceEncapsulateComponent<SelectValueUIService, SelectValueUIServiceImpl> {

    /**
     * Default constructor
     */
    public SelectValueUIComponent() {
        super(SelectValueUIService.class, new SelectValueUIServiceImpl());
    }
}
