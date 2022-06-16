package fr.dila.st.core.util;

import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doQueryForIds;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Classe utilitaire permettant d'effectuer une recherche de documents en désactivant la gestion des ACL.
 *
 * @author jtremeaux
 */
public class UnrestrictedQueryRunner extends UnrestrictedSessionRunner {
    private String queryString;

    private DocumentModelList resultList;

    private int limit = -1;

    private PrefetchInfo prefetchInfo;

    public UnrestrictedQueryRunner(CoreSession session, String queryString, String... schemas) {
        super(session);
        this.queryString = queryString;
        if (ArrayUtils.isNotEmpty(schemas)) {
            this.prefetchInfo = new PrefetchInfo(StringUtils.join(schemas, ","));
        }
    }

    @Override
    public void run() {
        resultList = session.getDocuments(doQueryForIds(session, queryString, limit, 0), prefetchInfo);
    }

    /**
     * Effectue la recherche et retourne la liste de résultats.
     *
     * @return Liste des résultats
     */
    public DocumentModelList findAll() {
        runUnrestricted();
        return resultList;
    }

    /**
     * Retourne le premier résultat de la recherche. Retourne null si aucun résultat, et le premier résultat si la
     * requête en retourne plusieurs.
     *
     * @return Premier résultat de la recherche
     */
    public DocumentModel getFirst() {
        limit = 1;
        runUnrestricted();
        limit = -1;
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
