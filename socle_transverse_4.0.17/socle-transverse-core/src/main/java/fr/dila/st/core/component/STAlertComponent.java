package fr.dila.st.core.component;

import fr.dila.st.api.service.STAlertService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STAlertServiceImpl;

public class STAlertComponent extends ServiceEncapsulateComponent<STAlertService, STAlertServiceImpl> {

    public STAlertComponent() {
        super(STAlertService.class, new STAlertServiceImpl());
    }
}
