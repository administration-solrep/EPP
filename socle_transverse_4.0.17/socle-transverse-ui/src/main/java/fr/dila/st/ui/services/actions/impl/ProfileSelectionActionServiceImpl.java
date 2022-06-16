package fr.dila.st.ui.services.actions.impl;

import fr.dila.st.api.user.STUser;
import fr.dila.st.ui.services.actions.ProfileSelectionActionService;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ProfileSelectionActionServiceImpl implements ProfileSelectionActionService {

    @Override
    public void addProfile(DocumentModel userDoc, String profile) {
        STUser user = userDoc.getAdapter(STUser.class);
        List<String> profilIdList = user.getGroups();
        if (!profilIdList.contains(profile)) {
            profilIdList.add(profile);
        }
        user.setGroups(profilIdList);
    }

    @Override
    public void removeProfile(DocumentModel userDoc, String profile) {
        STUser user = userDoc.getAdapter(STUser.class);
        List<String> profilIdList = user.getGroups();
        profilIdList.remove(profile);
        user.setGroups(profilIdList);
    }
}
