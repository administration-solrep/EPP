package fr.dila.st.core.component;

import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.VocabularyServiceImpl;

public class VocabularyComponent extends ServiceEncapsulateComponent<VocabularyService, VocabularyServiceImpl> {

    public VocabularyComponent() {
        super(VocabularyService.class, new VocabularyServiceImpl());
    }
}
