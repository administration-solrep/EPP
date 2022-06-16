package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator.getCorbeilleActionService;
import static fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator.getEvenementTypeActionService;
import static fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator.getMetadonneesActionService;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.solonepp.core.SolonEppFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
@Deploy("fr.dila.solonepp.ui")
public class SolonEppActionsServiceLocatorIT {

    /**
     * Vérifie que les action services sont bien instanciés/accessibles
     */
    @Test
    public void testServices() {
        assertThat(getCorbeilleActionService()).isInstanceOf(CorbeilleActionService.class);
        assertThat(getEvenementTypeActionService()).isInstanceOf(EvenementTypeActionService.class);
        assertThat(getMetadonneesActionService()).isInstanceOf(MetadonneesActionService.class);
    }
}
