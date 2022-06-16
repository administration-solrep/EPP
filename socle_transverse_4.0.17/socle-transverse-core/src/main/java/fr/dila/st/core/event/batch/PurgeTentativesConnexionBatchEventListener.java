package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Calendar;
import javax.persistence.EntityManager;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;

/**
 * Batch de purge de la table des tentatives de connexion.
 *
 * @author tlombard
 */
public class PurgeTentativesConnexionBatchEventListener extends AbstractBatchEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(PurgeTentativesConnexionBatchEventListener.class);

    private static final String QUERY_TRUNCATE_TABLE = "TRUNCATE TABLE TENTATIVES_CONNEXION";

    public PurgeTentativesConnexionBatchEventListener() {
        super(LOGGER, STEventConstant.BATCH_EVENT_PURGE_TENTATIVES_CONNEXION);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, STLogEnumImpl.INIT_B_PURGE_TENTATIVES_CONNEXION_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();

        getPersistenceProvider()
            .run(
                true,
                new PersistenceProvider.RunVoid() {

                    @Override
                    public void runWith(EntityManager em) {
                        em.createNativeQuery(QUERY_TRUNCATE_TABLE).executeUpdate();
                    }
                }
            );

        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Purge des tentatives de connexion",
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, STLogEnumImpl.END_B_PURGE_TENTATIVES_CONNEXION_TEC);
    }

    protected PersistenceProvider getPersistenceProvider() {
        Thread thread = Thread.currentThread();
        ClassLoader last = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
            PersistenceProviderFactory persistenceProviderFactory = ServiceUtil.getRequiredService(
                PersistenceProviderFactory.class
            );
            PersistenceProvider provider = persistenceProviderFactory.newProvider("bruteforce-infos");
            provider.openPersistenceUnit();
            return provider;
        } finally {
            thread.setContextClassLoader(last);
        }
    }
}
