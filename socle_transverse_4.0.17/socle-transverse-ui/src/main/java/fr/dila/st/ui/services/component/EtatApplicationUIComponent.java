package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.EtatApplicationUIService;
import fr.dila.st.ui.services.impl.EtatApplicationUIServiceImpl;

public class EtatApplicationUIComponent
    extends ServiceEncapsulateComponent<EtatApplicationUIService, EtatApplicationUIServiceImpl> {

    /**
     * Default constructor
     */
    public EtatApplicationUIComponent() {
        super(EtatApplicationUIService.class, new EtatApplicationUIServiceImpl());
    }
}
