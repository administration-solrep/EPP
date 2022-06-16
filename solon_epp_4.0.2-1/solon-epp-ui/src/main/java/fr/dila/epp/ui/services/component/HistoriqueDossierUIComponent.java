package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.HistoriqueDossierUIService;
import fr.dila.epp.ui.services.impl.HistoriqueDossierUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class HistoriqueDossierUIComponent
    extends ServiceEncapsulateComponent<HistoriqueDossierUIService, HistoriqueDossierUIServiceImpl> {

    /*
     * Default constructor
     */
    public HistoriqueDossierUIComponent() {
        super(HistoriqueDossierUIService.class, new HistoriqueDossierUIServiceImpl());
    }
}
