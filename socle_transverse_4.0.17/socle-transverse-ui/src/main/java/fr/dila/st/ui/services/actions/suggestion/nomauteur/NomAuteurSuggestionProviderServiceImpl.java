package fr.dila.st.ui.services.actions.suggestion.nomauteur;

import fr.dila.st.ui.services.actions.suggestion.SuggestionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.Normalizer;
import java.util.List;

public class NomAuteurSuggestionProviderServiceImpl implements NomAuteurSuggestionProviderService {
    private static final String NAME = "NOM_AUTEUR_PROVIDER";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Renvoie les suggestions de noms complets des auteurs, basé sur le contenu du
     * nom ou du prénom. Insensible à la casse et aux accents.
     */
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
            "select distinct nomCompletAuteur from " +
            "question Q where TRANSLATE(UPPER(nomAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?" +
            " OR TRANSLATE(UPPER(prenomAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?" +
            " OR TRANSLATE(UPPER(nomCompletAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?";

        return SuggestionHelper.buildSuggestionsList(
            "qu:nomCompletAuteur",
            context.getSession(),
            query,
            new Object[] { search, search, search }
        );
    }
}
