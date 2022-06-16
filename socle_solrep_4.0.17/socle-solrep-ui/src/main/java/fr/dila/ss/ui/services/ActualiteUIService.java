package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteDTO;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ActualiteUIService {
    DocumentModel toDocumentModel(SpecificContext context, ActualiteDTO dto);

    ActualiteConsultationDTO toActualiteForm(DocumentModel actualiteDoc);

    void removeActualites(SpecificContext context);

    ActualitesList getActualitesList(SpecificContext context);

    List<ActualiteConsultationDTO> fetchUserActualitesNonLues(SpecificContext context);

    void setActualiteLue(SpecificContext context);
}
