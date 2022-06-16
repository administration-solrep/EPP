package fr.dila.st.core.event.batch;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.util.BatchUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface CleanDeletedDocument {
    default long removeDeletedDocuments(
        CoreSession session,
        STLogger logger,
        BatchLoggerModel batchLoggerModel,
        List<String> documentTypes
    ) {
        logger.info(session, STLogEnumImpl.INIT_B_DEL_DOC_TEC);
        long nbError = 0L;
        for (String documentType : documentTypes) {
            logger.info(session, STLogEnumImpl.PROCESS_B_DEL_DOC_TEC, "Début suppression " + documentType);

            nbError += BatchUtil.removeDocDeletedState(session, documentType, batchLoggerModel);
            session.save();

            logger.info(session, STLogEnumImpl.PROCESS_B_DEL_DOC_TEC, "Fin suppression " + documentType);
        }
        logger.info(session, STLogEnumImpl.END_B_DEL_DOC_TEC);

        return nbError;
    }
}
