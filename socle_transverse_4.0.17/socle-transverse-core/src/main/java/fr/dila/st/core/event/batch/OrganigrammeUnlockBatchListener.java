package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

public class OrganigrammeUnlockBatchListener extends AbstractBatchEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(OrganigrammeUnlockBatchListener.class);

    public OrganigrammeUnlockBatchListener() {
        super(LOGGER, STEventConstant.UNLOCK_ORGANIGRAMME_BATCH_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, STLogEnumImpl.INIT_B_UNLOCK_ORGA_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        List<OrganigrammeNode> lockedNodes = organigrammeService.getLockedNodes();
        long nbNodeUnlocked = 0;
        for (OrganigrammeNode node : lockedNodes) {
            if (organigrammeService.unlockOrganigrammeNode(node)) {
                ++nbNodeUnlocked;
            } else {
                ++errorCount;
            }
        }
        session.save();
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre de noeuds déverrouillés",
                nbNodeUnlocked,
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, STLogEnumImpl.END_B_UNLOCK_ORGA_TEC);
    }
}
