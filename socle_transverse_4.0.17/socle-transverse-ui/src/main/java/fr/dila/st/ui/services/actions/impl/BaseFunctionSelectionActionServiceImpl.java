package fr.dila.st.ui.services.actions.impl;

import fr.dila.st.api.user.Profile;
import fr.dila.st.ui.services.actions.BaseFunctionSelectionActionService;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class BaseFunctionSelectionActionServiceImpl implements BaseFunctionSelectionActionService {

    public void addBaseFunction(DocumentModel profileDoc, String baseFunction) {
        Profile profile = profileDoc.getAdapter(Profile.class);
        List<String> baseFunctionList = profile.getBaseFunctionList();
        if (!baseFunctionList.contains(baseFunction)) {
            baseFunctionList.add(baseFunction);
            profile.setBaseFunctionList(baseFunctionList);
        }
    }

    public void removeBaseFunction(DocumentModel profileDoc, String baseFunction) {
        Profile profile = profileDoc.getAdapter(Profile.class);
        List<String> baseFunctionList = profile.getBaseFunctionList();
        if (baseFunctionList.contains(baseFunction)) {
            baseFunctionList.remove(baseFunction);
            profile.setBaseFunctionList(baseFunctionList);
        }
    }
}
