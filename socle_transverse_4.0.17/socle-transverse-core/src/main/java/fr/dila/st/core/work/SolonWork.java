package fr.dila.st.core.work;

import static java.lang.String.format;
import static java.time.Instant.now;

import fr.dila.st.api.work.WorkStatus;
import fr.dila.st.core.util.DateUtil;
import java.time.Instant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.work.AbstractWork;

public abstract class SolonWork extends AbstractWork {
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(SolonWork.class);

    public SolonWork() {
        super();
        initiateStatus();
    }

    @Override
    public String getTitle() {
        return this.getClass().getSimpleName();
    }

    private void initiateStatus() {
        setStatus(WorkStatus.PENDING.getStatus());
    }

    @Override
    public final void work() {
        setStatus(WorkStatus.EXECUTING.getStatus());
        LOG.debug("Début work " + getId());
        Instant start = now();
        doWork();
        setStatus(WorkStatus.DONE.getStatus());
        LOG.debug(format("Fin work %s. Temps d'exécution : %s", getId(), DateUtil.getDuration(start)));
    }

    protected abstract void doWork();
}
