package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STRechercheUtilisateursUIService;
import fr.dila.st.ui.services.impl.STRechercheUtilisateursUIServiceImpl;

public class STRechercheUtilisateursUIComponent
    extends ServiceEncapsulateComponent<STRechercheUtilisateursUIService, STRechercheUtilisateursUIServiceImpl> {

    /**
     * Default constructor
     */
    public STRechercheUtilisateursUIComponent() {
        super(STRechercheUtilisateursUIService.class, new STRechercheUtilisateursUIServiceImpl());
    }
}
