package fr.dila.ss.core.operation.distribution;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import fr.dila.ss.core.service.ActualiteServiceImpl;
import fr.dila.ss.core.test.ActualiteFeature;
import fr.dila.ss.core.test.SolrepFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolrepFeature.class, ActualiteFeature.class })
public class CreateActualiteRootOperationIT {
    @Inject
    private CoreSession session;

    @Inject
    private UserManager userManager;

    @Test
    public void testActualiteRootFolder() throws OperationException {
        assertThat(session.exists(ActualiteServiceImpl.ACTUALITES_ROOT_PATH_REF)).isTrue();

        try (
            CloseableCoreSession userSession = CoreInstance.openCoreSession(
                session.getRepositoryName(),
                userManager.getPrincipal("adminsgg")
            )
        ) {
            boolean hasPermission = userSession.hasPermission(
                ActualiteServiceImpl.ACTUALITES_ROOT_PATH_REF,
                SecurityConstants.READ_WRITE
            );

            assertThat(hasPermission).isTrue();
        }
    }
}
