package fr.dila.ss.core.component;

import fr.dila.ss.api.pdf.SSPdfService;
import fr.dila.ss.core.pdf.SSPdfServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSPdfComponent extends ServiceEncapsulateComponent<SSPdfService, SSPdfServiceImpl> {

    public SSPdfComponent() {
        super(SSPdfService.class, new SSPdfServiceImpl());
    }
}
