package fr.dila.st.core.component;

import fr.dila.st.api.service.FonctionService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.FonctionServiceImpl;

public class FonctionComponent extends ServiceEncapsulateComponent<FonctionService, FonctionServiceImpl> {

    public FonctionComponent() {
        super(FonctionService.class, new FonctionServiceImpl());
    }
}
