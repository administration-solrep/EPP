package fr.dila.st.core.component;

import fr.dila.st.api.service.ProfileService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.ProfileServiceImpl;

public class ProfileComponent extends ServiceEncapsulateComponent<ProfileService, ProfileServiceImpl> {

    public ProfileComponent() {
        super(ProfileService.class, new ProfileServiceImpl());
    }
}
