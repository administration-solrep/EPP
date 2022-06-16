package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STGouvernementUIService;
import fr.dila.st.ui.services.impl.STGouvernementUIServiceImpl;

public class STGouvernementUIComponent
    extends ServiceEncapsulateComponent<STGouvernementUIService, STGouvernementUIServiceImpl> {

    /**
     * Default constructor
     */
    public STGouvernementUIComponent() {
        super(STGouvernementUIService.class, new STGouvernementUIServiceImpl());
    }
}
