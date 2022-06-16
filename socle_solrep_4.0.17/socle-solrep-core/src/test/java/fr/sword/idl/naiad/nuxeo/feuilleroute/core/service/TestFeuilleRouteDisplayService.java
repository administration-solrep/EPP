package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteDisplayService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteFeature;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestConstants;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestUtils;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 *
 */
@RunWith(FeaturesRunner.class)
@Features(FeuilleRouteFeature.class)
public class TestFeuilleRouteDisplayService {
    @Inject
    private FeuilleRouteDisplayService service;

    @Test
    public void testGetRouteElements() {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            List<RouteTableElement> elements = service.getRouteElements(route, session);
            Assert.assertNotNull(elements);
            Assert.assertEquals(4, elements.size());
            for (RouteTableElement element : elements) {
                Assert.assertEquals(1, element.getRouteMaxDepth());
            }
            Assert.assertEquals(1, elements.get(2).getFirstChildFolders().size());
            Assert.assertEquals(0, elements.get(3).getFirstChildFolders().size());
            Assert.assertEquals(2, elements.get(2).getFirstChildFolders().get(0).getTotalChildCount());
            Assert.assertEquals(4, elements.get(0).getRouteTable().getTotalChildCount());
        }
    }
}
