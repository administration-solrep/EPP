package fr.dila.ss.core.birt;

import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.st.api.work.WorkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.work.AbstractWork;

public class BirtWork extends AbstractWork {
    private static final long serialVersionUID = 2754309927555664154L;

    private static final Logger LOGGER = LogManager.getLogger(BirtWork.class);

    private static final String BIRT_WORK_TITLE = "Génération de rapport Birt";

    private SolonBirtParameters birtParameters;

    public BirtWork(SolonBirtParameters birtParameters) {
        super();
        LOGGER.info("Création d'un nouveau BirtWork");
        this.birtParameters = birtParameters;
        setStatus(WorkStatus.PENDING.getStatus());
    }

    @Override
    public String getTitle() {
        return BIRT_WORK_TITLE;
    }

    @Override
    public void work() {
        setStatus(WorkStatus.EXECUTING.getStatus());

        new BirtAppCommand().call(birtParameters);

        setStatus(WorkStatus.DONE.getStatus());
        LOGGER.info("BirtWork traité");
    }

    protected SolonBirtParameters getBirtParameters() {
        return birtParameters;
    }
}
