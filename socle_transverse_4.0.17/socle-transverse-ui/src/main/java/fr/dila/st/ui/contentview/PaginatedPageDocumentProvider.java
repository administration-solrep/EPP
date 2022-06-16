package fr.dila.st.ui.contentview;

import fr.dila.st.api.constant.STContentViewConstant;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

/**
 * Enable to populate content view as with CoreQueryDocumentPageProvider use count(*) and pagination
 *
 * The pagination management is the same as CoreQueryDocumentPageProvider.
 *
 * Example of usage in contrib : <genericPageProvider class="fr.dila.st.web.contentview.PaginatedPageDocumentProvider">
 * <property name="coreSession">#{documentManager}</property> <property name="checkQueryCache">true</property> <pattern
 * quoteParameters="false" escapeParameters="false"> SELECT * FROM DossierLink WHERE ecm:parentId =
 * 'd4e35b5b-68c8-44eb-bb20-5bcf34c11478' AND ecm:isProxy=0 </pattern> <pageSize>10</pageSize> </genericPageProvider>
 *
 * @author SPL
 */
public class PaginatedPageDocumentProvider extends CoreQueryDocumentPageProvider {
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(PaginatedPageDocumentProvider.class);

    private static final String ADDITIONAL_SORT = "additionalSort";

    /**
     * Default constructor
     */
    public PaginatedPageDocumentProvider() {
        super();
    }

    @Override
    public List<DocumentModel> getCurrentPage() {
        checkQueryCache();
        if (currentPageDocuments == null) {
            error = null;
            errorMessage = null;

            CoreSession coreSession = getCoreSession();
            if (query == null) {
                buildQuery(coreSession);
            }
            if (query == null) {
                throw new NuxeoException(String.format("Cannot perform null query: check provider '%s'", getName()));
            }

            currentPageDocuments = new ArrayList<DocumentModel>();
            resultsCount = 0;

            if (!query.contains(STContentViewConstant.DEFAULT_EMPTY_QUERY)) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            String.format(
                                "Perform query for provider '%s': '%s' with pageSize=%s, offset=%s",
                                getName(),
                                query,
                                Long.valueOf(getPageSize()),
                                Long.valueOf(offset)
                            )
                        );
                    }

                    resultsCount = QueryUtils.doCountQuery(coreSession, query);
                    if (resultsCount > 0) {
                        // on fait la 2ieme requete que si le nombre de resultat est supérieur à 0
                        Long pagesize = getPageSize();
                        if (pagesize < 1) {
                            pagesize = maxPageSize;
                        }

                        currentPageDocuments = QueryUtils.doQuery(coreSession, query, pagesize, offset);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            String.format(
                                "Performed query for provider '%s': got %s hits",
                                getName(),
                                Long.valueOf(resultsCount)
                            )
                        );
                    }
                } catch (Exception e) {
                    error = e;
                    errorMessage = e.getMessage();
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return currentPageDocuments;
    }

    protected String getAdditionalSort() {
        return (String) getProperties().get(ADDITIONAL_SORT);
    }

    protected SortInfo[] getSortInfosWithAdditionalSort() {
        SortInfo[] sortArray = null;
        String additionalSort = getAdditionalSort();
        sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos, additionalSort);
        return sortArray;
    }

    @Override
    protected void buildQuery(CoreSession coreSession) {
        try {
            SortInfo[] sortArray = getSortInfosWithAdditionalSort();
            String newQuery;
            PageProviderDefinition def = getDefinition();
            if (def.getWhereClause() == null) {
                newQuery =
                    NXQLQueryBuilder.getQuery(
                        def.getPattern(),
                        getParameters(),
                        def.getQuotePatternParameters(),
                        def.getEscapePatternParameters(),
                        null,
                        sortArray
                    );
            } else {
                DocumentModel searchDocumentModel = getSearchDocumentModel();
                if (searchDocumentModel == null) {
                    throw new NuxeoException(
                        String.format(
                            "Cannot build query of provider '%s': " + "no search document model is set",
                            getName()
                        )
                    );
                }
                newQuery =
                    NXQLQueryBuilder.getQuery(searchDocumentModel, def.getWhereClause(), getParameters(), sortArray);
            }

            if (newQuery != null && !newQuery.equals(query)) {
                // query has changed => refresh
                refresh();
                query = OrderByDistinctQueryCorrector.correct(newQuery, sortArray);
            }
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }
}
