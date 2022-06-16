package fr.dila.st.core.component;

import fr.dila.st.api.service.STUserSearchService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STUserSearchServiceImpl;

public class STUserSearchComponent extends ServiceEncapsulateComponent<STUserSearchService, STUserSearchServiceImpl> {

    public STUserSearchComponent() {
        super(STUserSearchService.class, new STUserSearchServiceImpl());
    }
}
