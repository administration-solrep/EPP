package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getEvenementUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getHistoriqueDossierUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getMessageListUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getMetadonneesUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getNotificationUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getSelectValueUIService;
import static fr.dila.epp.ui.services.SolonEppUIServiceLocator.getVersionUIService;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.HistoriqueDossierUIService;
import fr.dila.epp.ui.services.MessageListUIService;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.epp.ui.services.VersionUIService;
import fr.dila.solonepp.core.SolonEppFeature;
import fr.dila.st.ui.services.NotificationUIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
@Deploy("fr.dila.solonepp.ui")
public class SolonEppUIServiceLocatorIT {

    /**
     * Vérifie que les services UI sont bien instanciés/accessibles
     */
    @Test
    public void testServices() {
        assertThat(getEvenementUIService()).isInstanceOf(EvenementUIService.class);
        assertThat(getHistoriqueDossierUIService()).isInstanceOf(HistoriqueDossierUIService.class);
        assertThat(getMessageListUIService()).isInstanceOf(MessageListUIService.class);
        assertThat(getMetadonneesUIService()).isInstanceOf(MetadonneesUIService.class);
        assertThat(getSelectValueUIService()).isInstanceOf(SelectValueUIService.class);
        assertThat(getVersionUIService()).isInstanceOf(VersionUIService.class);
        assertThat(getNotificationUIService()).isInstanceOf(NotificationUIService.class);
    }
}
