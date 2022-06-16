package fr.dila.st.core.component;

import fr.dila.st.api.service.CleanupService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.CleanupServiceImpl;

public class CleanupComponent extends ServiceEncapsulateComponent<CleanupService, CleanupServiceImpl> {

    public CleanupComponent() {
        super(CleanupService.class, new CleanupServiceImpl());
    }
}
