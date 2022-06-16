package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteFeature;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestConstants;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestUtils;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 */
@RunWith(FeaturesRunner.class)
@Features(FeuilleRouteFeature.class)
public class TestFeuilleRouteImpl {
    @Inject
    private CoreSession session;

    @Test
    public void testMethods() {
        FeuilleRoute routeModel = FeuilleRouteTestUtils
            .createDocumentRouteModel(session, FeuilleRouteTestConstants.ROUTE1, FeuilleRouteTestConstants.ROOT_PATH)
            .getAdapter(FeuilleRoute.class);
        Assert.assertNotNull(routeModel);
        Assert.assertEquals(FeuilleRouteTestConstants.ROUTE1, routeModel.getName());
    }
}
