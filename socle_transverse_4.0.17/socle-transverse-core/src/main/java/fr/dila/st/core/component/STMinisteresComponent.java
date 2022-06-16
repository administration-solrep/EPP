package fr.dila.st.core.component;

import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.organigramme.STMinisteresServiceImpl;

public class STMinisteresComponent extends ServiceEncapsulateComponent<STMinisteresService, STMinisteresServiceImpl> {

    public STMinisteresComponent() {
        super(STMinisteresService.class, new STMinisteresServiceImpl());
    }
}
