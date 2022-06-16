package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STSchemaConstant.USER_USERNAME;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;

import fr.dila.st.api.service.STUserSearchService;
import fr.dila.st.core.query.QueryHelper;
import java.util.Collection;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;

public class STUserSearchServiceImpl implements STUserSearchService {

    @Override
    public List<DocumentModel> getUsersFromIds(Collection<String> userIds) {
        return QueryHelper.getDocsFromIds(
            userIds,
            USER_USERNAME,
            p -> getUserManager().searchUsers(new QueryBuilder().predicate(p))
        );
    }
}
