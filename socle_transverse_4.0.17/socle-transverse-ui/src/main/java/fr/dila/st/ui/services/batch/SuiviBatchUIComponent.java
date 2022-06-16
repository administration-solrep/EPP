package fr.dila.st.ui.services.batch;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SuiviBatchUIComponent extends ServiceEncapsulateComponent<SuiviBatchUIService, SuiviBatchUIServiceImpl> {

    /**
     * Default constructor
     */
    public SuiviBatchUIComponent() {
        super(SuiviBatchUIService.class, new SuiviBatchUIServiceImpl());
    }
}
