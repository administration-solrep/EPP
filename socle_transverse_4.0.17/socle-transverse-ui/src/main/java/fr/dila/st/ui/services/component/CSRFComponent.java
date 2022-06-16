package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.CSRFService;
import fr.dila.st.ui.services.impl.CSRFServiceImpl;

public class CSRFComponent extends ServiceEncapsulateComponent<CSRFService, CSRFServiceImpl> {

    /**
     * Default constructor
     */
    public CSRFComponent() {
        super(CSRFService.class, new CSRFServiceImpl());
    }
}
