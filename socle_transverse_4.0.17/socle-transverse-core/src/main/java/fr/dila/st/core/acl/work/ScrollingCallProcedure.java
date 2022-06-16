package fr.dila.st.core.acl.work;

import fr.dila.st.core.work.SolonWork;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.work.api.WorkManager;

/**
 * Worker qui lance plusieurs workers avec chacun un group d'ids d'utilisateurs
 * pour appeler une procédure stockée
 *
 */
public class ScrollingCallProcedure extends SolonWork {
    private static final long serialVersionUID = 1L;

    private final String procedure;
    private final LinkedList<String> usernames;

    private int bucketSize = 200;

    private static final Log LOG = LogFactory.getLog(ScrollingCallProcedure.class);

    public ScrollingCallProcedure(String procedure, Collection<String> usernames) {
        this(procedure, usernames, -1);
    }

    public ScrollingCallProcedure(String procedure, Collection<String> usernames, int bucketSize) {
        super();
        Objects.requireNonNull(procedure);
        Objects.requireNonNull(usernames);

        this.procedure = procedure;
        this.usernames = new LinkedList<>(usernames);
        if (bucketSize > 0) {
            this.bucketSize = bucketSize;
        }
    }

    @Override
    public String getTitle() {
        return "ScrollingCallProcedure";
    }

    @Override
    protected void doWork() {
        openSystemSession();

        LOG.info(usernames.size() + " utilisateurs à traiter");

        WorkManager workManager = ServiceUtil.getRequiredService(WorkManager.class);
        ListUtils
            .partition(usernames, bucketSize)
            .stream()
            .map(p -> new CallProcedure(procedure, p))
            .forEach(workManager::schedule);
    }
}
