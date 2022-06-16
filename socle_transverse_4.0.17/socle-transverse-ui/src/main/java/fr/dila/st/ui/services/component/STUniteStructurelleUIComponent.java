package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STUniteStructurelleUIService;
import fr.dila.st.ui.services.impl.STUniteStructurelleUIServiceImpl;

public class STUniteStructurelleUIComponent
    extends ServiceEncapsulateComponent<STUniteStructurelleUIService, STUniteStructurelleUIServiceImpl> {

    /**
     * Default constructor
     */
    public STUniteStructurelleUIComponent() {
        super(STUniteStructurelleUIService.class, new STUniteStructurelleUIServiceImpl());
    }
}
