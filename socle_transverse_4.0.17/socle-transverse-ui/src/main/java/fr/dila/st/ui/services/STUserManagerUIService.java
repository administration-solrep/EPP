package fr.dila.st.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public interface STUserManagerUIService {
    boolean getAllowEditUser(SpecificContext context);

    boolean getAllowChangePassword(SpecificContext context);

    boolean getAllowCreateUser(SpecificContext context);

    boolean getAllowDeleteUser(SpecificContext context);

    boolean isCurrentUserPermanent(NuxeoPrincipal principal);

    boolean isNotReadOnly();

    boolean isReadOnly();

    void createUser(SpecificContext context);

    void updateUser(SpecificContext context);

    void deleteUser(SpecificContext context);

    void initUserContext(SpecificContext context);

    void initUserCreationContext(SpecificContext context);

    void updatePassword(SpecificContext context);

    void askResetPassword(SpecificContext context);
}
