package fr.dila.st.ui.services.actions.suggestion;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface ISuggestionProvider {
    String getName();

    List<String> getSuggestions(String input, SpecificContext context);
}
