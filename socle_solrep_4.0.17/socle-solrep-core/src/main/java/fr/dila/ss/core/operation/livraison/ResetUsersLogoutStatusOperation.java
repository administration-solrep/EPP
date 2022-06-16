package fr.dila.ss.core.operation.livraison;

import fr.dila.ss.api.service.SSUserService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import java.util.List;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.usermanager.UserManager;

@STVersion(version = "4.0.4", application = STApplication.REPONSES)
@STVersion(version = "4.0.1", application = STApplication.EPG)
@Operation(
    id = ResetUsersLogoutStatusOperation.ID,
    category = "User",
    label = "ResetUsersLogoutStatus",
    description = "Reset du statut logout de tous les utilisateurs connectés"
)
public class ResetUsersLogoutStatusOperation extends AbstractPersistenceDefaultComponent {
    public static final String ID = "Users.Status.Logout.Reset";

    private static final STLogger LOG = STLogFactory.getLog(ResetUsersLogoutStatusOperation.class);

    @Context
    private CoreSession session;

    @Context
    private UserManager userManager;

    @Context
    private SSUserService userService;

    @OperationMethod
    public void run() {
        LOG.info(STLogEnumImpl.DEFAULT, "Début de l'opération " + ID);

        List<STUser> users = userService.setLogoutTrueForAllUsers();
        LOG.info(session, STLogEnumImpl.UPDATE_USER_TEC, String.format("%d utilisateurs mis à jour", users.size()));

        LOG.info(STLogEnumImpl.DEFAULT, "Fin de l'opération " + ID);
    }
}
