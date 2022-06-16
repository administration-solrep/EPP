package fr.dila.st.ui.services;

import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface STOrganigrammeManagerService {
    void computeOrganigrammeActions(SpecificContext context);

    List<SuggestionDTO> getSuggestions(SpecificContext context);
}
