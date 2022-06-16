package fr.dila.ss.ui.services.actions.suggestion.feuilleroute;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FeuilleRouteSuggestionProviderComponent
    extends ServiceEncapsulateComponent<FeuilleRouteSuggestionProviderService, FeuilleRouteSuggestionProviderServiceImpl> {

    /**
     * Default constructor
     */
    public FeuilleRouteSuggestionProviderComponent() {
        super(FeuilleRouteSuggestionProviderService.class, new FeuilleRouteSuggestionProviderServiceImpl());
    }
}
