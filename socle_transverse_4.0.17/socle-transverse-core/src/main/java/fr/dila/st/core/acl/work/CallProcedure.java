package fr.dila.st.core.acl.work;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.work.SolonWork;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.security.SecurityService;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class CallProcedure extends SolonWork {
    private static final long serialVersionUID = -7127372114836742346L;

    private static final Log LOG = LogFactory.getLog(CallProcedure.class);

    private final String procedure;
    private final LinkedList<String> usernames;

    public CallProcedure(String procedure, Collection<String> usernames) {
        super();
        Objects.requireNonNull(procedure);
        Objects.requireNonNull(usernames);

        this.procedure = procedure;
        this.usernames = new LinkedList<>(usernames);
    }

    @Override
    public String getTitle() {
        return "CallProcedure";
    }

    @Override
    protected void doWork() {
        openSystemSession();
        LOG.debug(String.format("%s : call %s pour %d utilisateur(s)", getId(), procedure, usernames.size()));
        LOG.trace(String.format("%s : utilisateur(s) :%n%s", getId(), usernames));

        UserManager um = STServiceLocator.getUserManager();
        usernames.stream().map(um::getPrincipal).filter(Objects::nonNull).forEach(this::callProcedure);
    }

    private void callProcedure(NuxeoPrincipal principal) {
        String[] principalsToCheck = SecurityService.getPrincipalsToCheck(principal);
        LOG.trace(
            String.format(
                "call %s pour l'utilisateur %s avec les groupes %s",
                procedure,
                principal.getName(),
                Arrays.asList(principalsToCheck)
            )
        );
        QueryUtils.execSqlFunction(session, procedure, new Object[] { principalsToCheck });
    }
}
