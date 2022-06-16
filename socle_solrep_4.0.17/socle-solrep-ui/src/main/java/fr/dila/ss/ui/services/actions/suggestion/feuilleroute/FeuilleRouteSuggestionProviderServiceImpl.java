package fr.dila.ss.ui.services.actions.suggestion.feuilleroute;

import fr.dila.st.ui.services.actions.suggestion.SuggestionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.Normalizer;
import java.util.List;

public class FeuilleRouteSuggestionProviderServiceImpl implements FeuilleRouteSuggestionProviderService {
    private static final String NAME = "FEUILLE_DE_ROUTE_PROVIDER";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> getSuggestions(String input, SpecificContext context) {
        String search =
            "%" +
            Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toUpperCase() +
            "%";

        String query =
            "select distinct dublincore.title " +
            "from feuille_route, dublincore where feuille_route.id = dublincore.id and feuille_route.typecreation is null " +
            " and TRANSLATE(UPPER(dublincore.title), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?";

        return SuggestionHelper.buildSuggestionsList("dc:title", context.getSession(), query, new Object[] { search });
    }
}
