package fr.dila.st.core.service;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.user.BaseFunction;
import java.util.Optional;
import org.nuxeo.ecm.core.api.DocumentModel;

public class FonctionServiceImpl implements FonctionService {

    @Override
    public BaseFunction getFonction(String fonctionName) {
        DocumentModel fonction = STServiceLocator
            .getVocabularyService()
            .getEntry(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR, fonctionName);
        return Optional.ofNullable(fonction).map(e -> e.getAdapter(BaseFunction.class)).orElse(null);
    }
}
