package fr.dila.st.core.event.batch;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import java.util.LinkedList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

/**
 *
 * Squelette du batch de suppression des documents à l'état deleted
 *
 */
public class CleanDeletedDocumentBatchListener extends AbstractBatchEventListener implements CleanDeletedDocument {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CleanDeletedDocumentBatchListener.class);

    private List<String> documentTypes;

    /**
     * Constructeur
     *
     * @param eventName
     *            le nom de l'évènement pour lancer le batch
     * @param documentType
     *            le type de document à supprimer
     */
    protected CleanDeletedDocumentBatchListener(String eventName, String documentType) {
        super(LOGGER, eventName);
        this.documentTypes = new LinkedList<String>();
        this.documentTypes.add(documentType);
    }

    /**
     * Constructeur
     *
     * @param eventName
     *            le nom de l'évènement pour lancer le batch
     * @param documentType
     *            le type de document à supprimer
     */
    protected CleanDeletedDocumentBatchListener(String eventName, List<String> documentTypes) {
        super(LOGGER, eventName);
        this.documentTypes = new LinkedList<String>(documentTypes);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        errorCount += removeDeletedDocuments(session, LOGGER, batchLoggerModel, documentTypes);
    }
}
