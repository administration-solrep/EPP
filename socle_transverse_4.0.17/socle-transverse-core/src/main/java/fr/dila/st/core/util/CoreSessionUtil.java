package fr.dila.st.core.util;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Objects;
import java.util.function.Consumer;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;

public final class CoreSessionUtil {

    private CoreSessionUtil() {}

    /**
     * Vérifie que la session n'est pas nulle, sinon renvoie une NullPointerException.
     */
    public static void assertSessionNotNull(final CoreSession session) {
        Objects.requireNonNull(session, "La session ne peut pas être nulle");
    }

    public static CloseableCoreSession openSession(CoreSession session, String username) {
        return CoreInstance.openCoreSession(session.getRepositoryName(), getUserManager().getPrincipal(username));
    }

    public static void doAction(String username, Consumer<CoreSession> consumer) {
        NuxeoPrincipal principal = getUserManager().getPrincipal(username);
        try (CloseableCoreSession userSession = openSession(principal)) {
            consumer.accept(userSession);
        }
    }

    private static CloseableCoreSession openSession(NuxeoPrincipal principal) {
        String repo = getRepo();
        if (principal == null) {
            return CoreInstance.openCoreSessionSystem(repo);
        } else {
            return CoreInstance.openCoreSession(repo, principal);
        }
    }

    public static String getRepo() {
        return ServiceUtil.getRequiredService(RepositoryManager.class).getDefaultRepositoryName();
    }
}
