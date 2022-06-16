package fr.dila.st.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;

public interface PasswordService extends FragmentService {
    boolean updatePassword(SpecificContext context, String newPassword);

    void askResetPassword(SpecificContext context);
}
