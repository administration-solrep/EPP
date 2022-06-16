package fr.dila.st.api.event.batch;

import fr.dila.st.api.util.BatchHelper;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface commune à tous les batchs
 *
 * @author tlombard
 */
public interface STBatch {
    /**
     * Indique si le batch peut s'exécuter. Méthode destinée à être surchargée.
     */
    default boolean canBatchBeLaunched(final CoreSession session) {
        return canBatchBeLaunched();
    }

    default boolean canBatchBeLaunched() {
        return BatchHelper.canBatchBeLaunched();
    }
}
