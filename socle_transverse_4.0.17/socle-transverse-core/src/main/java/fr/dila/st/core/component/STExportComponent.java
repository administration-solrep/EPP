package fr.dila.st.core.component;

import fr.dila.st.api.service.STExportService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STExportServiceImpl;

public class STExportComponent extends ServiceEncapsulateComponent<STExportService, STExportServiceImpl> {

    public STExportComponent() {
        super(STExportService.class, new STExportServiceImpl());
    }
}
