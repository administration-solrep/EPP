package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.services.impl.STUtilisateursUIServiceImpl;

public class STUtilisateursUIServiceComponent
    extends ServiceEncapsulateComponent<STUtilisateursUIService, STUtilisateursUIServiceImpl> {

    /**
     * Default constructor
     */
    public STUtilisateursUIServiceComponent() {
        super(STUtilisateursUIService.class, new STUtilisateursUIServiceImpl());
    }
}
