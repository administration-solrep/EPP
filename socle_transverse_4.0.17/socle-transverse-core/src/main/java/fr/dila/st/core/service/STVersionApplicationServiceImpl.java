package fr.dila.st.core.service;

import fr.dila.st.api.service.STVersionApplicationService;
import javax.persistence.Query;

public class STVersionApplicationServiceImpl
    extends AbstractPersistenceDefaultComponent
    implements STVersionApplicationService {
    private static final String QUERY =
        "SELECT * FROM (SELECT LIBELLE FROM DATABASE_VERSION ORDER BY VERSION_DATE DESC) WHERE ROWNUM = 1";

    @Override
    public String getVersionApp() {
        return apply(
            true,
            entityManager -> {
                Query query = entityManager.createNativeQuery(QUERY);
                return (String) query.getSingleResult();
            }
        );
    }
}
