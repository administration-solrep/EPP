package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSModeleFdrFicheUIService;
import fr.dila.ss.ui.services.impl.SSModeleFdrFicheUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSModeleFdrFicheUIComponent
    extends ServiceEncapsulateComponent<SSModeleFdrFicheUIService, SSModeleFdrFicheUIServiceImpl> {

    /**
     * Default constructor
     */
    public SSModeleFdrFicheUIComponent() {
        super(SSModeleFdrFicheUIService.class, new SSModeleFdrFicheUIServiceImpl());
    }
}
