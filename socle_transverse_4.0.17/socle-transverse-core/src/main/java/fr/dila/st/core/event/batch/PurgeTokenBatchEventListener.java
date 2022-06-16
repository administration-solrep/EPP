package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.STTokenService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

/**
 * Batch de purge des token de connexion invalides
 *
 * @author slefevre
 */
public class PurgeTokenBatchEventListener extends AbstractBatchEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(PurgeTokenBatchEventListener.class);

    public PurgeTokenBatchEventListener() {
        super(LOGGER, STEventConstant.BATCH_EVENT_PURGE_TOKEN);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        STTokenService tokenService = STServiceLocator.getTokenService();
        tokenService.purgeInvalidTokens();
        session.save();
    }
}
