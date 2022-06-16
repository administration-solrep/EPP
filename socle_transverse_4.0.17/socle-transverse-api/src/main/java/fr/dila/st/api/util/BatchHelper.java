package fr.dila.st.api.util;

import static org.nuxeo.runtime.api.Framework.getService;

import fr.dila.st.api.service.EtatApplicationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BatchHelper {
    private static final Log LOG = LogFactory.getLog(BatchHelper.class);

    private BatchHelper() {}

    public static boolean canBatchBeLaunched() {
        boolean canBeLaunched = !getService(EtatApplicationService.class).isApplicationTechnicallyRestricted();
        if (!canBeLaunched) {
            LOG.info("Les batchs sont désactivés suite à la restriction technique");
        }
        return canBeLaunched;
    }
}
