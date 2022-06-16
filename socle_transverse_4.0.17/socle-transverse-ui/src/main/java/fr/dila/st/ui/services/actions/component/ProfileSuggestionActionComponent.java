package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.ProfileSuggestionActionService;
import fr.dila.st.ui.services.actions.impl.ProfileSuggestionActionServiceImpl;

public class ProfileSuggestionActionComponent
    extends ServiceEncapsulateComponent<ProfileSuggestionActionService, ProfileSuggestionActionServiceImpl> {

    /**
     * Default constructor
     */
    public ProfileSuggestionActionComponent() {
        super(ProfileSuggestionActionService.class, new ProfileSuggestionActionServiceImpl());
    }
}
