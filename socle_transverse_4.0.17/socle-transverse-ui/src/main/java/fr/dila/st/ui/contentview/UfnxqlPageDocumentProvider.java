package fr.dila.st.ui.contentview;

import fr.dila.st.core.helper.PaginationHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

/**
 * Ce provider permet d'encapsuler une requête UFNXQL comportant des paramètres dynamiques (nombre de paramètres
 * variables) en gérant le tri et la pagination.
 *
 * Il faut passer 2 propriétés : - queryString : requête à utiliser (ex: SELECT d.ecm:uuid from Document as d WHERE
 * d.dc:title = ?) - parameters : tableau des paramètres
 *
 * Afin de gérer les requêtes dynamiques, ces 2 propriétés peuvent provenir d'un Bean Seam (syntaxe #{bean.propriete}).
 *
 * Exemple de contribution au content provider : <genericPageProvider
 * class="fr.dila.st.web.contentview.UfnxqlPageDocumentProvider"> <property
 * name="coreSession">#{documentManager}</property> <property name="checkQueryCache">true</property> <property
 * name="queryString">#{xyBean.queryString}</property> <property name="parameters">#{xyBean.queryParameter}</property>
 * <pageSize>10</pageSize> </genericPageProvider>
 *
 * @author jtremeaux
 */
public class UfnxqlPageDocumentProvider extends CoreQueryDocumentPageProvider {
    /**
     * Paramètre du provider contenant la chaîne de la requête.
     */
    public static final String QUERY_STRING_PROPERTY = "queryString";

    /**
     * Paramètre du provider contenent les paramètres de la requête. Les paramètres peuvent être une collection, un
     * tableau d'objet ou non renseignés.
     */
    public static final String QUERY_PARAMETER_PROPERTY = "parameters";

    public static final String TYPE_DOCUMENT = "typeDocument";

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(UfnxqlPageDocumentProvider.class);

    /**
     * Default constructor
     */
    public UfnxqlPageDocumentProvider() {
        super();
    }

    @Override
    public List<DocumentModel> getCurrentPage() {
        checkQueryCache();

        if (currentPageDocuments == null) {
            error = null;
            errorMessage = null;

            final CoreSession coreSession = getCoreSession();
            if (query == null) {
                buildQuery(coreSession);
            }
            if (query == null) {
                throw new NuxeoException(String.format("Cannot perform null query: check provider '%s'", getName()));
            }

            currentPageDocuments = new ArrayList<DocumentModel>();

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

                Object[] params = null;
                final Map<String, Serializable> props = getProperties();
                final Object queryParameterProp = props.get(QUERY_PARAMETER_PROPERTY);
                if (queryParameterProp != null) {
                    if (queryParameterProp instanceof Collection) {
                        params = ((Collection<?>) queryParameterProp).toArray();
                    } else if (queryParameterProp instanceof Object[]) {
                        params = (Object[]) queryParameterProp;
                    } else {
                        throw new NuxeoException("La propriété \"parameters\" doit être un tableau d'objets");
                    }
                }
                String typeDocument = (String) props.get(TYPE_DOCUMENT);
                Objects.requireNonNull(typeDocument);
                resultsCount =
                    QueryUtils.doCountQuery(coreSession, FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query, params);
                Long pagesize = getPageSize();
                if (pagesize < 1) {
                    pagesize = maxPageSize;
                }

                if (resultsCount > 0) {
                    // récupération page courante
                    offset = PaginationHelper.calculeOffSet(offset, pagesize, resultsCount);

                    currentPageDocuments =
                        QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                            coreSession,
                            typeDocument,
                            query,
                            params,
                            pagesize,
                            offset
                        );
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
            } catch (final Exception e) {
                error = e;
                errorMessage = e.getMessage();

                if (LOG.isWarnEnabled()) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return currentPageDocuments;
    }

    @Override
    protected void buildQuery(final CoreSession coreSession) {
        SortInfo[] sortArray = null;
        if (sortInfos != null) {
            sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos);
        }

        final Map<String, Serializable> props = getProperties();
        final Object queryStringProp = props.get(QUERY_STRING_PROPERTY);
        if (!(queryStringProp instanceof String)) {
            throw new NuxeoException("La propriété \"queryString\" doit être renseignée");
        }
        final String queryString = (String) queryStringProp;
        final String newQuery = NXQLQueryBuilder.getQuery(queryString, null, false, false, null, sortArray);

        if (newQuery != null && !newQuery.equals(query)) {
            // query has changed => refresh
            refresh();
            query = newQuery;
        }
    }
}
