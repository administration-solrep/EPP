package fr.dila.st.core.operation.utils;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import javax.persistence.Query;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Opération permettant de mettre à jour la version d'une application dans la table DATABASE_VERSION de l'application concerné
 *
 * Exemple d'appel dans le nxshell : Update.Version.Application -version '4.0.0'
 *
 */
@Operation(
    id = UpdateVersionOperation.ID,
    label = "Ajouter la version de l'application dans la table DATABASE_VERSION",
    description = UpdateVersionOperation.DESCRIPTION
)
public class UpdateVersionOperation extends AbstractPersistenceDefaultComponent {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Update.Version.Application";

    public static final String DESCRIPTION =
        "Cette opération ajoute la version de l'application dans la table DATABASE_VERSION";

    private static final STLogger LOGGER = STLogFactory.getLog(UpdateVersionOperation.class);

    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    @Param(name = "version")
    protected String version;

    @OperationMethod
    public void run() {
        LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Ajout de la version " + version);

        accept(
            true,
            entityManager -> {
                Query selectQuery = entityManager.createNativeQuery("SELECT * FROM DATABASE_VERSION WHERE LIBELLE = ?");
                selectQuery.setParameter(1, version);
                if (selectQuery.getResultList().isEmpty()) {
                    Query insertQuery = entityManager.createNativeQuery(
                        "INSERT INTO DATABASE_VERSION VALUES (?, SYSDATE)"
                    );
                    insertQuery.setParameter(1, version);
                    insertQuery.executeUpdate();
                }
            }
        );
    }
}
