package fr.dila.st.core.component;

import fr.dila.st.api.service.STDirectoryService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STDirectoryServiceImpl;

public class STDirectoryComponent extends ServiceEncapsulateComponent<STDirectoryService, STDirectoryServiceImpl> {

    public STDirectoryComponent() {
        super(STDirectoryService.class, new STDirectoryServiceImpl());
    }
}
