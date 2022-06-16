package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.event.AbstractBatchPostCommitEventListener;
import fr.dila.st.core.event.batch.CleanDeletedDocument;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import java.util.Arrays;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

public class CleanDeletedStepBatchListener
    extends AbstractBatchPostCommitEventListener
    implements CleanDeletedDocument {
    private static final STLogger LOGGER = STLogFactory.getLog(CleanDeletedStepBatchListener.class);

    @Override
    protected void handleEvent(Event event, CoreSession session) {
        removeDeletedDocuments(session, LOGGER, null, getDocumentTypes());
    }

    private static final List<String> getDocumentTypes() {
        return Arrays.asList(
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
            SSConstant.FEUILLE_ROUTE_MODEL_FOLDER_DOCUMENT_TYPE
        );
    }

    @Override
    protected boolean accept(Event event) {
        return true;
    }
}
