package fr.dila.st.core.caselink;

import static fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker.COL_COUNT;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doSqlQuery;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.work.SolonWork;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public class RemoveDuplicateCaseLinksForCaseWork extends SolonWork {
    private static final long serialVersionUID = 79697611581716938L;

    private static final STLogger LOG = STLogFactory.getLog(RemoveDuplicateCaseLinksForCaseWork.class);

    private static final String QUERY_COUNT_DUPLICATE_CASE_LINKS =
        "Select COUNT_DUPLICATE_CASE_LINKS_FOR_CASEID(?) as count From DUAL";
    private static final String REMOVE_DUPLICATE_CASE_LINKS_PROCEDURE = "REMOVE_DUPLICATE_CASE_LINKS_FOR_CASEID(?)";

    private final String caseDocId;

    public RemoveDuplicateCaseLinksForCaseWork(String caseDocId) {
        super();
        Objects.requireNonNull(caseDocId);
        this.caseDocId = caseDocId;
    }

    @Override
    protected void doWork() {
        openSystemSession();
        try (
            IterableQueryResult res = doSqlQuery(
                session,
                new String[] { COL_COUNT },
                QUERY_COUNT_DUPLICATE_CASE_LINKS,
                new String[] { caseDocId }
            )
        ) {
            Iterator<Map<String, Serializable>> iterator = res.iterator();
            if (iterator.hasNext() && (Long) iterator.next().get(COL_COUNT) > 0L) {
                LOG.info(
                    STLogEnumImpl.DEFAULT,
                    "Des dossiers link en double pour le dossier [" +
                    caseDocId +
                    "] ont été repérés et seront supprimés."
                );
                QueryUtils.execSqlFunction(session, REMOVE_DUPLICATE_CASE_LINKS_PROCEDURE, new String[] { caseDocId });
                QueryHelper.invalidateAllCache(session);
            }
        }
    }
}
