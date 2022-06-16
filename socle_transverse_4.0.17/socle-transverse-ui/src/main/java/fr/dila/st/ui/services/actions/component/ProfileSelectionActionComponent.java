package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.ProfileSelectionActionService;
import fr.dila.st.ui.services.actions.impl.ProfileSelectionActionServiceImpl;

public class ProfileSelectionActionComponent
    extends ServiceEncapsulateComponent<ProfileSelectionActionService, ProfileSelectionActionServiceImpl> {

    /**
     * Default constructor
     */
    public ProfileSelectionActionComponent() {
        super(ProfileSelectionActionService.class, new ProfileSelectionActionServiceImpl());
    }
}
