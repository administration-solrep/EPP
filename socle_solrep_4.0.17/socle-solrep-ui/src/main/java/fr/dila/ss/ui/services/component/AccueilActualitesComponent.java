package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.AccueilActualitesService;
import fr.dila.ss.ui.services.impl.AccueilActualitesServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class AccueilActualitesComponent
    extends ServiceEncapsulateComponent<AccueilActualitesService, AccueilActualitesServiceImpl> {

    public AccueilActualitesComponent() {
        super(AccueilActualitesService.class, new AccueilActualitesServiceImpl());
    }
}
