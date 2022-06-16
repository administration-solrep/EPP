package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSDerniersElementsComponentService;
import fr.dila.ss.ui.services.impl.SSDerniersElementsComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSDerniersElementsComponent
    extends ServiceEncapsulateComponent<SSDerniersElementsComponentService, SSDerniersElementsComponentServiceImpl> {

    /**
     * Default constructor
     */
    public SSDerniersElementsComponent() {
        super(SSDerniersElementsComponentService.class, new SSDerniersElementsComponentServiceImpl());
    }
}
