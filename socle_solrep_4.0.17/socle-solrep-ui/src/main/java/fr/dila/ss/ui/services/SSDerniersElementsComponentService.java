package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.DernierElementDTO;
import fr.dila.st.ui.services.FragmentService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;

public interface SSDerniersElementsComponentService extends FragmentService {
    @Override
    Map<String, Object> getData(SpecificContext context);

    List<DernierElementDTO> getDernierElementsDTOFromDocs(SpecificContext context);
}
