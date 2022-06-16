package fr.dila.ss.ui.services;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.ss.core.test.SolrepFeature;
import fr.dila.ss.ui.services.actions.suggestion.feuilleroute.FeuilleRouteSuggestionProviderService;
import fr.dila.ss.ui.services.comment.SSCommentManagerUIService;
import fr.dila.ss.ui.services.organigramme.SSMigrationManagerUIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolrepFeature.class })
@Deploy({ "fr.dila.ss.ui", "fr.dila.st.ui" })
public class SSUIServiceLocatorIT {

    @Test
    public void testServices() {
        assertThat(SSUIServiceLocator.getActualiteUIService()).isInstanceOf(ActualiteUIService.class);
        assertThat(SSUIServiceLocator.getSSFeuilleRouteUIService()).isInstanceOf(SSFeuilleRouteUIService.class);
        assertThat(SSUIServiceLocator.getSSMigrationManagerActionService())
            .isInstanceOf(SSMigrationManagerUIService.class);
        assertThat(SSUIServiceLocator.getSSOrganigrammeManagerService())
            .isInstanceOf(SSOrganigrammeManagerService.class);
        assertThat(SSUIServiceLocator.getProfilUIService()).isInstanceOf(ProfilUIService.class);
        assertThat(SSUIServiceLocator.getSSCommentManagerUIService()).isInstanceOf(SSCommentManagerUIService.class);
        assertThat(SSUIServiceLocator.getSSArchiveUIService()).isInstanceOf(SSArchiveUIService.class);
        assertThat(SSUIServiceLocator.getSSModeleFdrFicheUIService()).isInstanceOf(SSModeleFdrFicheUIService.class);
        assertThat(SSUIServiceLocator.getFeuilleRouteSuggestionProviderService())
            .isInstanceOf(FeuilleRouteSuggestionProviderService.class);
        assertThat(SSUIServiceLocator.getSSFdrUIService()).isInstanceOf(SSFdrUIService.class);
    }
}
