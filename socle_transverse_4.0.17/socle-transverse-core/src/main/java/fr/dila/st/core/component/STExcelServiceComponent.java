package fr.dila.st.core.component;

import fr.dila.st.api.service.STExcelService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STExcelServiceImpl;

public class STExcelServiceComponent extends ServiceEncapsulateComponent<STExcelService, STExcelServiceImpl> {

    public STExcelServiceComponent() {
        super(STExcelService.class, new STExcelServiceImpl());
    }
}
