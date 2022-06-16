package fr.dila.ss.core.operation.livraison;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.service.FeuilleRouteTreePersister;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

@STVersion(version = "4.0.3", application = STApplication.REPONSES)
@STVersion(version = "4.0.0", application = STApplication.EPG)
@Operation(
    id = DeleteFdrInstanceReady.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Suppression des instance de feuille de route au statut ready",
    description = "Correction du ticket M137084 - Suppression des instance de feuille de route au statut ready"
)
public class DeleteFdrInstanceReady extends AbstractPersistenceDefaultComponent {
    public static final String ID = "SolonEpg.Suppression.Instance.Fdr.Ready";

    private static final STLogger LOG = STLogFactory.getLog(DeleteFdrInstanceReady.class);

    private static final String QUERY =
        "SELECT fr.ecm:uuid as id FROM FeuilleRoute as fr " +
        "WHERE fr.ecm:parentId = ? " +
        "AND fr.ecm:currentLifeCycleState = 'ready'";

    @Context
    private CoreSession session;

    @Context
    private NuxeoPrincipal principal;

    public DeleteFdrInstanceReady() {}

    @OperationMethod
    public void run() {
        LOG.info(
            STLogEnumImpl.DEFAULT,
            "Début de l'opération de suppression des instance de feuille de route au statut ready"
        );

        FeuilleRoutePersister persister = new FeuilleRouteTreePersister();
        final List<String> params = new ArrayList<>();
        params.add(persister.getOrCreateRootOfDocumentRouteInstanceStructure(session).getId());
        DocumentRef[] idRefList = QueryUtils.doUFNXQLQueryForIds(session, QUERY, params.toArray());
        session.removeDocuments(idRefList);
        session.save();

        LOG.info(
            STLogEnumImpl.DEFAULT,
            "Fin de l'opération de suppression des instance de feuille de route au statut ready : " +
            idRefList.length +
            " instances supprimées."
        );
    }
}
