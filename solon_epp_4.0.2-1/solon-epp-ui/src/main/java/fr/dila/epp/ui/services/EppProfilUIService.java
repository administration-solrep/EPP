package fr.dila.epp.ui.services;

import fr.dila.epp.ui.th.bean.ProfilForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface EppProfilUIService {
    void saveProfil(SpecificContext context);

    ProfilForm getProfil(SpecificContext context);
}
