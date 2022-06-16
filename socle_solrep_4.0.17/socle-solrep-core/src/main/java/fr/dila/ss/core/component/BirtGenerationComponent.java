package fr.dila.ss.core.component;

import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.core.birt.BirtGenerationServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class BirtGenerationComponent
    extends ServiceEncapsulateComponent<BirtGenerationService, BirtGenerationServiceImpl> {

    public BirtGenerationComponent() {
        super(BirtGenerationService.class, new BirtGenerationServiceImpl());
    }
}
