package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSTemplateUIService;
import fr.dila.ss.ui.services.impl.SSTemplateUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSTemplateUIComponent extends ServiceEncapsulateComponent<SSTemplateUIService, SSTemplateUIServiceImpl> {

    public SSTemplateUIComponent() {
        super(SSTemplateUIService.class, new SSTemplateUIServiceImpl());
    }
}
