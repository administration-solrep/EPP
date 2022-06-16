package fr.dila.st.ui.th.model;

import static fr.dila.st.ui.enums.STActionCategory.FILTERED_ACTIONS;
import static fr.dila.st.ui.enums.STActionCategory.MAIN_MENU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.th.model.bean.ActionBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class, SolonMockitoFeature.class })
@Deploy("org.nuxeo.ecm.actions:OSGI-INF/actions-framework.xml")
@Deploy("fr.dila.st.ui.test:OSGI-INF/test-st-actions-contrib.xml")
public class SpecificContextActionIT {
    @Inject
    private CoreFeature coreFeature;

    @Before
    public void before() {}

    @Test
    public void actionManagerTest() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            SpecificContext context = new SpecificContext();
            context.setSession(session);
            List<Action> actions = context.getActions(MAIN_MENU);
            assertNotNull(actions);
            assertEquals(1, actions.size());
            assertEquals("main_admin", actions.get(0).getId());
            assertEquals("/admin", actions.get(0).getLink());
        }
    }

    @Test
    public void filterActionTest() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            SpecificContext context = new SpecificContext();
            context.setSession(session);

            List<Action> actions = context.getActions(FILTERED_ACTIONS);
            assertNotNull(actions);
            assertEquals(0, actions.size());

            Map<String, Object> contextMap = new HashMap<>();
            contextMap.put("monAction", new ActionBean(true));
            context.setContextData(contextMap);

            actions = context.getActions(FILTERED_ACTIONS);
            assertNotNull(actions);
            assertEquals(1, actions.size());
            assertEquals("main_admin_filter", actions.get(0).getId());
            assertEquals("/adminFiltered", actions.get(0).getLink());

            contextMap = new HashMap<>();
            contextMap.put("monAction", new ActionBean(false));
            context.setContextData(contextMap);

            actions = context.getActions(FILTERED_ACTIONS);
            assertNotNull(actions);
            assertEquals(0, actions.size());
        }
    }
}
