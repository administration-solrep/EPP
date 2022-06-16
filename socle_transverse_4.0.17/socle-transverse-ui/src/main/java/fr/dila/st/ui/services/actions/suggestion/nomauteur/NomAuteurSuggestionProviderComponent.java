package fr.dila.st.ui.services.actions.suggestion.nomauteur;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class NomAuteurSuggestionProviderComponent
    extends ServiceEncapsulateComponent<NomAuteurSuggestionProviderService, NomAuteurSuggestionProviderServiceImpl> {

    /**
     * Default constructor
     */
    public NomAuteurSuggestionProviderComponent() {
        super(NomAuteurSuggestionProviderService.class, new NomAuteurSuggestionProviderServiceImpl());
    }
}
