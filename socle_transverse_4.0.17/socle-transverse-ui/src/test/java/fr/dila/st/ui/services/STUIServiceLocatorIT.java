package fr.dila.st.ui.services;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.test.STFeature;
import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;
import fr.dila.st.ui.services.batch.SuiviBatchUIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STFeature.class })
@Deploy("fr.dila.st.ui")
@BlacklistComponent({ "fr.dila.st.ui.STMinistereUIService", "fr.dila.st.ui.STUniteStructurelleUIService" })
public class STUIServiceLocatorIT {

    @Test
    public void testServices() {
        assertThat(STUIServiceLocator.getSuiviBatchUIService()).isInstanceOf(SuiviBatchUIService.class);
        assertThat(STUIServiceLocator.getEtatApplicationUIService()).isInstanceOf(EtatApplicationUIService.class);
        assertThat(STUIServiceLocator.getNomAuteurSuggestionProviderService())
            .isInstanceOf(NomAuteurSuggestionProviderService.class);
    }
}
