package fr.dila.st.core.component;

import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.organigramme.STPostesServiceImpl;

public class STPostesComponent extends ServiceEncapsulateComponent<STPostesService, STPostesServiceImpl> {

    public STPostesComponent() {
        super(STPostesService.class, new STPostesServiceImpl());
    }
}
