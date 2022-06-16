package fr.dila.ss.ui.services.impl;

import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.contentview.ModeleFDRPageProvider;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSModeleFdrListUIService;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;

public class SSModeleFdrListUIServiceImpl implements SSModeleFdrListUIService {
    private static final STLogger LOG = STLogFactory.getLog(SSModeleFdrListUIServiceImpl.class);

    @Override
    public ModeleFDRList getModelesFDRSubstitution(SpecificContext context) {
        ModeleFDRList modeleFDRList = new ModeleFDRList();
        return getModelesFDRSubstitution(modeleFDRList, context);
    }

    @Override
    public ModeleFDRList getModelesFDRSubstitution(ModeleFDRList modeleFDRList, SpecificContext context) {
        try {
            ModeleFDRListForm form = (ModeleFDRListForm) Optional
                .ofNullable(context.getFromContextData(SSContextDataKey.LIST_MODELE_FDR))
                .orElse(new ModeleFDRListForm());
            ModeleFDRPageProvider provider = buildModeleFDRSubstitutionProvider("modeleFDRPageProvider", context, form);

            modeleFDRList.buildColonnesSubstitution(form);
            modeleFDRList.setIsSubstitutionTable(true);
            modeleFDRList.setHasPagination(true);

            // On fait le mapping des documents vers notre DTO
            modeleFDRList.setListe(
                provider
                    .getCurrentPage()
                    .stream()
                    .filter(FeuilleRouteDTO.class::isInstance)
                    .map(FeuilleRouteDTO.class::cast)
                    .collect(Collectors.toList())
            );
            modeleFDRList.setNbTotal((int) provider.getResultsCount());
        } catch (Exception e) {
            LOG.error(
                context.getSession(),
                SSLogEnumImpl.FAIL_GET_MOD_FDR_TEC,
                "Une erreur est survenue lors de la récupération des modèles de feuille de route",
                e
            );
            throw e;
        }
        return modeleFDRList;
    }

    public ModeleFDRPageProvider buildModeleFDRSubstitutionProvider(
        String pageProviderName,
        SpecificContext context,
        ModeleFDRListForm form
    ) {
        CoreSession session = context.getSession();

        FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        String feuilleRouteModelFolderId = feuilleRouteModelService.getFeuilleRouteModelFolderId(session);
        ModeleFeuilleRouteActionService modeleFeuilleRouteActionService = SSActionsServiceLocator.getModeleFeuilleRouteActionService();

        List<Object> lstParams = Arrays.asList(
            feuilleRouteModelFolderId,
            modeleFeuilleRouteActionService.getContentViewCriteriaSubstitution(context)
        );

        return form.getPageProvider(session, pageProviderName, "fdr.", lstParams);
    }
}
