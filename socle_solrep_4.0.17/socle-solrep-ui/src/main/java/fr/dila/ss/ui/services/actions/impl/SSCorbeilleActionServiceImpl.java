package fr.dila.ss.ui.services.actions.impl;

import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STCorbeilleService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class SSCorbeilleActionServiceImpl implements SSCorbeilleActionService {
    private static final STLogger LOGGER = STLogFactory.getLog(SSCorbeilleActionServiceImpl.class);

    @Override
    public boolean isDossierLoadedInCorbeille(SpecificContext context) {
        // Vérifie que le DossierLink est présent
        return getCurrentDossierLink(context) != null;
    }

    @Override
    public STDossierLink getCurrentDossierLink(SpecificContext context) {
        List<DocumentModel> dossiersLinks = getCurrentDossierLinkDocs(context, false);
        STDossierLink dossLink = null;

        if (dossiersLinks.size() == 1) {
            dossLink = dossiersLinks.get(0).getAdapter(STDossierLink.class);
        }

        return dossLink;
    }

    @Override
    public boolean hasCurrentDossierLinks(SpecificContext context) {
        return CollectionUtils.isNotEmpty(getCurrentDossierLinkDocs(context, true));
    }

    @Override
    public List<STDossierLink> getCurrentDossierLinks(SpecificContext context) {
        return getCurrentDossierLinkDocs(context, true)
            .stream()
            .map(doc -> doc.getAdapter(STDossierLink.class))
            .collect(Collectors.toList());
    }

    private List<DocumentModel> getCurrentDossierLinkDocs(SpecificContext context, boolean isUnrestricted) {
        String idDossierLink = context.getFromContextData(STContextDataKey.CURRENT_DOSSIER_LINK);
        DocumentModel dossierDoc = context.getCurrentDocument();
        List<DocumentModel> dossiersLinks = new ArrayList<>();

        if (StringUtils.isNotBlank(idDossierLink)) {
            try {
                dossiersLinks.add(context.getSession().getDocument(new IdRef(idDossierLink)));
            } catch (Exception e) {
                LOGGER.warn(context.getSession(), STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, e);
            }
        }

        if (CollectionUtils.isEmpty(dossiersLinks) && dossierDoc != null) {
            STCorbeilleService corbeilleService = STServiceLocator.getCorbeilleService();
            if (isUnrestricted) {
                dossiersLinks = corbeilleService.findDossierLinkUnrestricted(context.getSession(), dossierDoc.getId());
            } else {
                dossiersLinks = corbeilleService.findDossierLink(context.getSession(), dossierDoc.getId());
            }
        }
        return dossiersLinks;
    }
}
