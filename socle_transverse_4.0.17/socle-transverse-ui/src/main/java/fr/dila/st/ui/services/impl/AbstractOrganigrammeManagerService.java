package fr.dila.st.ui.services.impl;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.SizeLimitExceededException;

public abstract class AbstractOrganigrammeManagerService implements STOrganigrammeManagerService {
    private static final STLogger LOG = STLogFactory.getLog(AbstractOrganigrammeManagerService.class);

    protected static final String MAILBOX_TYPE = "MAILBOX_TYPE";

    public static final String INPUT_KEY = "inputVal";
    public static final String TYPE_SELECTION_KEY = "typeSelectionVal";

    @Override
    public List<SuggestionDTO> getSuggestions(SpecificContext context) {
        List<String> typeSelection = new ArrayList<>();
        String input = "";
        boolean activatePosteFilter = false;
        // On récupère le type de sélection dans le contexte
        if (context.getFromContextData(TYPE_SELECTION_KEY) != null) {
            typeSelection = context.getFromContextData(TYPE_SELECTION_KEY);
        }
        // On récupère l'input dans le contexte
        if (context.getFromContextData(INPUT_KEY) != null) {
            input = context.getFromContextData(INPUT_KEY);
        }
        if (context.getFromContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY) != null) {
            activatePosteFilter = context.getFromContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY);
        }

        return getSuggestions(context.getSession(), input, typeSelection, activatePosteFilter);
    }

    private List<SuggestionDTO> getSuggestions(
        CoreSession session,
        String input,
        List<String> selectionType,
        boolean activatePosteFilter
    ) {
        if (StringUtils.isBlank(input) || CollectionUtils.isEmpty(selectionType)) {
            return Collections.emptyList();
        }

        List<SuggestionDTO> out = getSuggestionsFromDirectoryNames(session, input, selectionType, activatePosteFilter);

        if (selectionType.contains(OrganigrammeType.USER.getValue())) {
            // Cas spécial pour les utilisateurs
            getUserSuggestions(input)
                .stream()
                .map(userDoc -> userDoc.getAdapter(STUser.class))
                .filter(STUser::isActive)
                .map(user -> new SuggestionDTO(user.getUsername(), user.getFullNameWithUsername()))
                .forEachOrdered(out::add);
        }

        return out;
    }

    protected abstract List<SuggestionDTO> getSuggestionsFromDirectoryNames(
        CoreSession session,
        String input,
        List<String> selectionType,
        boolean activatePosteFilter
    );

    private List<DocumentModel> getUserSuggestions(String input) {
        try {
            String searchPattern = input;
            return STServiceLocator.getUserManager().searchUsers(searchPattern);
        } catch (SizeLimitExceededException e) {
            LOG.error(STLogEnumImpl.FAIL_GET_USER_TEC, e);
            return Collections.emptyList();
        }
    }
}
