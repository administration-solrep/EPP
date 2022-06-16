package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.st.ui.th.model.SpecificContext;

public interface SSFdrUIService {
    FdrDTO getFeuilleDeRoute(SpecificContext context);
}
