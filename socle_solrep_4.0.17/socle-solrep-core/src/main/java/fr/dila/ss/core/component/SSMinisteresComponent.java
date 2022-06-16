package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSMinisteresService;
import fr.dila.ss.core.service.SSMinisteresServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSMinisteresComponent extends ServiceEncapsulateComponent<SSMinisteresService, SSMinisteresServiceImpl> {

    public SSMinisteresComponent() {
        super(SSMinisteresService.class, new SSMinisteresServiceImpl());
    }
}
