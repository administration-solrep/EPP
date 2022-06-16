package fr.dila.st.core.component;

import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.SuiviBatchServiceImpl;

public class SuiviBatchComponent extends ServiceEncapsulateComponent<SuiviBatchService, SuiviBatchServiceImpl> {

    public SuiviBatchComponent() {
        super(SuiviBatchService.class, new SuiviBatchServiceImpl());
    }
}
