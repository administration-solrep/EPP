package fr.dila.st.core.operation.utils;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.blob.DocumentBlobManager;
import org.nuxeo.ecm.core.blob.binary.BinaryManagerStatus;

/**
 * @author SCE
 *
 */
@Operation(
    id = GarbageCollectBinariesOperation.ID,
    category = "Binaries",
    label = "GarbageCollectBinariesOperation",
    description = "Supprime les binaires orphelins"
)
public class GarbageCollectBinariesOperation {
    public static final String ID = "Garbage.Collect.Binaries";

    private static final Log LOG = LogFactory.getLog(GarbageCollectBinariesOperation.class);

    @Param(name = "delete", required = false)
    private boolean delete = true;

    @OperationMethod
    public void run() {
        LOG.info("Début de l'opération " + ID);

        DocumentBlobManager docBlobManager = ServiceUtil.getRequiredService(DocumentBlobManager.class);
        if (!docBlobManager.isBinariesGarbageCollectionInProgress()) {
            BinaryManagerStatus binaryManagerStatus = docBlobManager.garbageCollectBinaries(delete);
            LOG.info("Statut des binaires orphelins: " + binaryManagerStatus);
        } else {
            LOG.info("La suppression des binaires orphelins est déjà en cours d'exécution");
        }

        LOG.info("Fin de l'opération " + ID);
    }
}
