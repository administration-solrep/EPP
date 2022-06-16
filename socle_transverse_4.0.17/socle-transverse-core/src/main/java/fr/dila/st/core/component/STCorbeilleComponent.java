package fr.dila.st.core.component;

import fr.dila.st.api.service.STCorbeilleService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STCorbeilleServiceImpl;

public class STCorbeilleComponent extends ServiceEncapsulateComponent<STCorbeilleService, STCorbeilleServiceImpl> {

    public STCorbeilleComponent() {
        super(STCorbeilleService.class, new STCorbeilleServiceImpl());
    }
}
