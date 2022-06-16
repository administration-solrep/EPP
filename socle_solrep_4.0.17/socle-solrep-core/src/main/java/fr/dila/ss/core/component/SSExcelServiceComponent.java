package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSExcelService;
import fr.dila.ss.core.service.SSExcelServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSExcelServiceComponent extends ServiceEncapsulateComponent<SSExcelService, SSExcelServiceImpl> {

    public SSExcelServiceComponent() {
        super(SSExcelService.class, new SSExcelServiceImpl());
    }
}
