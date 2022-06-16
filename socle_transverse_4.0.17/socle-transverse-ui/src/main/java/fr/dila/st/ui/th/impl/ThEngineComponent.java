package fr.dila.st.ui.th.impl;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.th.ThEngineService;
import org.nuxeo.runtime.model.ComponentInstance;

public class ThEngineComponent extends ServiceEncapsulateComponent<ThEngineService, ThEngineServiceImpl> {
    public static final String EP_CONFIG = "config";

    public ThEngineComponent() {
        super(ThEngineService.class, new ThEngineServiceImpl());
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (EP_CONFIG.equals(extensionPoint)) {
            ThEngineDescriptor descr = (ThEngineDescriptor) contribution;
            getServiceImpl().setConfiguration(descr);
        }
    }
}
