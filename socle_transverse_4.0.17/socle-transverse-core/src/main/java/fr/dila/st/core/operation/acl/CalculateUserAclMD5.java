package fr.dila.st.core.operation.acl;

import static fr.dila.st.core.service.STServiceLocator.getSTUserService;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.st.core.acl.work.ScrollingCallProcedure;
import fr.dila.st.core.operation.STVersion;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.work.api.WorkManager;

/**
 * @author SCE
 *
 */
@Operation(
    id = CalculateUserAclMD5.ID,
    category = "ACL",
    label = "CalculateUserAclMD5",
    description = "Appelle une procédure stockée pour remplir une table liée aux acls"
)
@STVersion(version = "4.0.0")
public class CalculateUserAclMD5 {
    public static final String ID = "ACL.CalculateUserMD5";

    public static final String SW_PREPARE_USERS_MD5_PROC = "SW_PREPARE_USERS_MD5(?)";

    private static final Log LOG = LogFactory.getLog(CalculateUserAclMD5.class);

    @Context
    private CoreSession session;

    @OperationMethod
    public void run() {
        LOG.info("Début de l'opération " + ID);

        List<String> activeUsernames = getSTUserService().getActiveUsernames();
        ScrollingCallProcedure work = new ScrollingCallProcedure(SW_PREPARE_USERS_MD5_PROC, activeUsernames);
        getRequiredService(WorkManager.class).schedule(work);

        LOG.info("Fin de l'opération " + ID);
    }
}
