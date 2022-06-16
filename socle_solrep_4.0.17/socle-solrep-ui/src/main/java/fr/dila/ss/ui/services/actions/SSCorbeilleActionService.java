package fr.dila.ss.ui.services.actions;

import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.ui.services.actions.STCorbeilleActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface SSCorbeilleActionService extends STCorbeilleActionService {
    STDossierLink getCurrentDossierLink(SpecificContext context);

    boolean hasCurrentDossierLinks(SpecificContext context);

    List<STDossierLink> getCurrentDossierLinks(SpecificContext context);
}
