package fr.dila.st.core.event;

import fr.dila.st.api.event.batch.STBatch;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.runtime.api.Framework;

public abstract class AbstractBatchPostCommitEventListener extends AbstractPostCommitEventListener implements STBatch {

    @Override
    public void handleEvent(EventBundle events) {
        if (Framework.isDevModeSet() || !canBatchBeLaunched()) {
            return;
        }
        super.handleEvent(events);
    }
}
