package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;

/**
 * Batch de déverrouillage des dossiers
 *
 *
 */
public class DossierUnlockBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DossierUnlockBatchListener.class);

    /**
     * Requête récupérant les documents locké
     */
    private static final String GET_LOCKED_DOCUMENT_QUERY =
        "SELECT * FROM Document where " +
        STSchemaConstant.ECM_SCHEMA_PREFIX +
        ":" +
        STSchemaConstant.ECM_LOCK_CREATED_PROPERTY +
        " <= TIMESTAMP '%s'";

    public DossierUnlockBatchListener() {
        super(LOGGER, STEventConstant.UNLOCK_BATCH_EVENT);
    }

    /**
     * Récupère le délai pour lequel on supprime les verrous. Par défaut, ce délai vaut 0 heure
     *
     * @return
     */
    protected String getDelai(CoreSession session) {
        return "0";
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, STLogEnumImpl.INIT_B_UNLOCK_DOC_TEC);
        long nbDocUnlocked = 0;
        // on récupère la date courante
        Calendar currentDate = Calendar.getInstance();

        // calcul de la date au dessous de laquelle on déverrouille les documents
        currentDate.add(Calendar.HOUR, -Integer.parseInt(getDelai(session)));
        Long timeInMillis = currentDate.getTimeInMillis();
        try {
            String dateLiteral = DateLiteral.dateTimeFormatter.print(timeInMillis);

            // requete utilisée
            String query = String.format(GET_LOCKED_DOCUMENT_QUERY, dateLiteral);

            // lancement de la requête
            DocumentModelList documentToUnlock = session.query(query);

            // parcourt des documents à déverrouiller
            if (documentToUnlock != null && documentToUnlock.size() > 0) {
                final STLockService stLockService = STServiceLocator.getSTLockService();
                for (DocumentModel documentModel : documentToUnlock) {
                    // déverrouillage des documents
                    if (stLockService.unlockDocUnrestricted(session, documentModel)) {
                        nbDocUnlocked++;
                    } else {
                        errorCount++;
                    }
                }
                session.save();
            }
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_B_UNLOCK_DOC_TEC, exc);
            errorCount++;
        }
        final long endTime = Calendar.getInstance().getTimeInMillis();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            // Ajout du log détaillé du batch
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre de dossiers déverrouillés",
                nbDocUnlocked,
                endTime - timeInMillis
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }

        LOGGER.info(session, STLogEnumImpl.END_B_UNLOCK_DOC_TEC);
    }
}
