package fr.dila.ss.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;

public interface NavigationActionService {
    boolean isFromEspaceTravail(SpecificContext context);

    boolean isFromAdmin(SpecificContext context);
}
