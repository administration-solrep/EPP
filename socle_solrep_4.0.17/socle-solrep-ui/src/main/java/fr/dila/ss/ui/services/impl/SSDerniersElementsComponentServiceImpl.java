package fr.dila.ss.ui.services.impl;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.DernierElementDTO;
import fr.dila.ss.ui.bean.DerniersElementsDTO;
import fr.dila.ss.ui.services.SSDerniersElementsComponentService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSDerniersElementsComponentServiceImpl implements SSDerniersElementsComponentService {
    public static final String SESSION_KEY = "derniersElements";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        DerniersElementsDTO dto = new DerniersElementsDTO();

        context.putInContextData(SESSION_KEY, getListeDerniersDossiersIntervention(context));

        dto.setDerniersElementsList(getDernierElementsDTOFromDocs(context));

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("derniersElementsMap", dto);
        returnMap.put("titleLabel", "derniersElements.label");

        context.getWebcontext().getUserSession().put(SESSION_KEY, dto);
        return returnMap;
    }

    protected List<DocumentModel> getListeDerniersDossiersIntervention(SpecificContext context) {
        return SSServiceLocator
            .getSsProfilUtilisateurService()
            .getListeDerniersDossiersIntervention(context.getSession());
    }

    @Override
    public List<DernierElementDTO> getDernierElementsDTOFromDocs(SpecificContext context) {
        List<DocumentModel> docs = context.getFromContextData(SESSION_KEY);
        return docs.stream().map(this::getDernierElementDTOFromDoc).collect(Collectors.toList());
    }

    private DernierElementDTO getDernierElementDTOFromDoc(DocumentModel doc) {
        DernierElementDTO dto = new DernierElementDTO();
        dto.setLabel(doc.getName());
        dto.setId(doc.getId());
        return dto;
    }
}
