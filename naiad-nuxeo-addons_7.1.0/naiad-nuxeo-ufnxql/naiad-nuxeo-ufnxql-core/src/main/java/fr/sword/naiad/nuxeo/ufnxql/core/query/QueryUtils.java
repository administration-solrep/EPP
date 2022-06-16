package fr.sword.naiad.nuxeo.ufnxql.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PartialList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.CountMapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.ListMapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.Mapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.NullMapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.RowMapper;

/**
 * Execute des requetes en utilisant le langage FNXQL.
 * 
 * Les methodes statique permettent d'executer des requetes en langage NXQL en les encapsulant dans
 * le langage FNXQL qui permet de faire des count(*) et des accès unrestricted en utilisant une session
 * standard.
 * 
 * @author spesnel
 * 
 */
public final class QueryUtils {

    public static final String FACET_PREFIX = "facet:";
    public static final String TYPE_NOT = "!";
    public static final String TYPE_AND = "&";
    public static final String TYPE_OR = "|";

    private static final Log LOG = LogFactory.getLog(QueryUtils.class);

    private static final String ERROR_MSG_COL_MISSING_FMT = "Result map should contains '%s' (probably 'AS %s' missing)";

    /**
     * Do count query over NXQL Query
     * 
     * @param session
     * @param queryNXQL NXQL or FNXQL query
     * @return the number of data matching the query
     * @throws NuxeoException
     */
    public static Long doCountQuery(final CoreSession session, final String queryNXQL) throws NuxeoException {
        return doCountQuery(session, queryNXQL, null);
    }

    /**
     * Do count query over NXQL Query with params
     * 
     * @param session
     * @param queryNXQL NXQL or FNXQL query
     * @return the number of data matching the query
     * @throws NuxeoException
     */
    public static Long doCountQuery(final CoreSession session, final String queryNXQL, final Object[] params)
            throws NuxeoException {
        final String fnxqlQuery = FlexibleQueryMaker.KeyCode.COUNT_CODE.getKey() + queryNXQL;
        return doFNXQLQueryAndMapping(session, fnxqlQuery, params, new CountMapper());
    }

    /**
     * Do count query over NXQL Query without ACL test
     * 
     * @param session
     * @param queryNXQL
     * @return the number of data matching the query
     * @throws NuxeoException
     */
    public static Long doUnrestrictedCountQuery(final CoreSession session, final String queryNXQL)
            throws NuxeoException {
        final String query = FlexibleQueryMaker.KeyCode.NO_DB_ACL_CHECK_CODE.getKey()
                + FlexibleQueryMaker.KeyCode.COUNT_CODE.getKey() + queryNXQL;
        // the FNXQL query skip the ACL test
        return doCountQuery(session, query);
    }

    /**
     * Do a queryAndFetch call without ACL test
     * 
     * @param session
     * @param queryNXQL
     * @return the number of data matching the query
     * @throws NuxeoException
     */
    public static IterableQueryResult doUnrestrictedQueryAndFetch(final CoreSession session, final String queryNXQL)
            throws NuxeoException {
        final String query = FlexibleQueryMaker.KeyCode.NO_DB_ACL_CHECK_CODE.getKey() + queryNXQL;
        // the FNXQL query skip the ACL test
        return doFNXQLQuery(session, query);
    }

    public static IterableQueryResult doQueryAndFetchPaginated(final CoreSession session, final String queryNXQL,
            final long limit, final long offset) throws NuxeoException {
        if (limit == 0) {
            throw new IllegalArgumentException("litmit should not be 0");
        }
        final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.getKey()).append('[')
                .append(limit).append(']').append('[').append(offset).append(']').append(queryNXQL);
        return doFNXQLQuery(session, strBuilder.toString());
    }

    /**
     * Do pagination on UFNXQL query
     * 
     * @param session
     * @param queryUFNXQL
     * @param limit
     * @param offset
     * @param params
     * @return
     * @throws NuxeoException
     */
    public static IterableQueryResult doQueryAndFetchPaginatedForUFNXQL(final CoreSession session,
            final String queryUFNXQL, final long limit, final long offset, final Object[] params)
                    throws NuxeoException {
        if (limit == 0) {
            throw new IllegalArgumentException("litmit should not be 0");
        }
        final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.getKey()).append('[')
                .append(limit).append(']').append('[').append(offset).append(']')
                .append(FlexibleQueryMaker.KeyCode.UFXNQL.getKey()).append(queryUFNXQL);
        return doFNXQLQuery(session, strBuilder.toString(), params);
    }

    /**
     * Retrieve document ids without acl check
     * 
     * @param session
     * @param queryNXQL
     * @param limit
     * @param offset
     * @param countTotal
     * @return
     * @throws NuxeoException
     */
    public static PartialList<Serializable> doUnrestrictedQueryForIds(final CoreSession session, final String queryNXQL,
            final long limit, final long offset, final boolean countTotal) throws NuxeoException {
        final IterableQueryResult res = doUnrestrictedQueryAndFetch(session, queryNXQL);
        try {

            if (offset > 0) {
                res.skipTo(offset);
            }

            final Iterator<Map<String, Serializable>> it = res.iterator();

            long toLoad = limit;
            final List<Serializable> ids = new ArrayList<Serializable>();
            while (it.hasNext() && toLoad > 0) {
                final Map<String, Serializable> m = it.next();
                final Serializable id = m.get(FlexibleQueryMaker.COL_ID);
                if (id == null) {
                    throw new NuxeoException(String.format(ERROR_MSG_COL_MISSING_FMT, FlexibleQueryMaker.COL_ID,
                            FlexibleQueryMaker.COL_ID));
                }
                ids.add(id);
                --toLoad;
            }

            final long totalSize = res.size();

            return new PartialList<Serializable>(ids, totalSize);
        } finally {
            res.close();
        }
    }

    /**
     * Retrieve document ids with pagination in query.
     * If required add pagination in FNXQL
     * 
     * @param session
     * @param queryNXQL NXQL ou FNXQL query
     * @param limit
     * @param offset
     * @param params parameter in the query
     * @return
     * @throws NuxeoException
     */
    public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL, final long limit,
            final long offset, final Object[] params) throws NuxeoException {
        String query = null;
        if (limit > 0) {
            final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.getKey());
            addParam(strBuilder, Long.toString(limit));
            addParam(strBuilder, Long.toString(offset));
            strBuilder.append(queryNXQL);
            query = strBuilder.toString();
        } else {
            query = queryNXQL;
        }

        return doFNXQLQueryAndMapping(session, query, params, new RowMapper<String>() {
            @Override
            public String doMapping(final Map<String, Serializable> rowData) throws NuxeoException {
                Serializable id = rowData.get(FlexibleQueryMaker.COL_ID);
                if (id == null) {
                    id = rowData.get("ecm:uuid");
                    if (id == null) {
                        throw new NuxeoException(String.format(ERROR_MSG_COL_MISSING_FMT, FlexibleQueryMaker.COL_ID,
                                FlexibleQueryMaker.COL_ID));
                    }
                }
                return (String) id;
            }
        });
    }

    /**
     * Retrieve document ids with pagination in query.
     * If required add pagination in FNXQL
     * 
     * @param session
     * @param queryNXQL NXQL ou FNXQL query
     * @param limit
     * @param offset
     * @return
     * @throws NuxeoException
     */
    public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL, final long limit,
            final long offset) throws NuxeoException {
        return doQueryForIds(session, queryNXQL, limit, offset, null);
    }

    /**
     * Retrieve document ids with pagination in query
     * 
     * @param session
     * @param queryNXQL NXQL ou FNXQL query
     * @return
     * @throws NuxeoException
     */
    public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL) throws NuxeoException {
        return doQueryForIds(session, queryNXQL, 0, 0, null);
    }

    /**
     * Execute une requete SQL
     * 
     * @param session
     * @param returnTypes type de retour de la requete SQL, exprime en schema ou mot cle
     *          (exemple dc:title, FlexibleQueryMaker.COL_COUNT, FlexibleQueryMaker.COL_ID). Utilisé pour le mapping
     * @param sqlQuery la requete SQL
     * @param params Contient les parametres eventuel de la requete SQL (representé dans la requete par un ?)
     * @return un objet IterableQueryResult à fermer par l'utilisateur
     * @throws NuxeoException
     */
    public static IterableQueryResult doSqlQuery(final CoreSession session, final String[] returnTypes,
            final String sqlQuery, final Object[] params) throws NuxeoException {
        final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.SQL_CODE.getKey());
        addParams(strBuilder, returnTypes);
        strBuilder.append(sqlQuery);

        return doFNXQLQuery(session, strBuilder.toString(), params);
    }

    /**
     * execute une fonction SQL
     */
    public static void execSqlFunction(final CoreSession session, final String functionCall, final Object[] params)
            throws NuxeoException {
        final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.SQL_CODE.getKey());
        addParams(strBuilder, new String[] { FlexibleQueryMaker.COL_ID });
        strBuilder.append("CALL ").append(functionCall);
        doFNXQLQueryAndMapping(session, strBuilder.toString(), params, new NullMapper());
    }

    /**
     * Execute une requete SQL et rajoute la pagination
     * 
     * @param session
     * @param returnTypes type de retour de la requete SQL, exprime en schema ou mot cle
     *          (exemple dc:title, FlexibleQueryMaker.COL_COUNT, FlexibleQueryMaker.COL_ID). Utilisé pour le mapping
     * @param sqlQuery la requete SQL
     * @param params Contient les parametres eventuel de la requete SQL (representé dans la requete par un ?)
     * @param limit nombre de resultat max
     * @param offset offset à partir duquel renvoyé les resultats
     * @return un objet IterableQueryResult à fermer par l'utilisateur
     * @throws NuxeoException
     */
    public static IterableQueryResult doSqlQuery(final CoreSession session, final String[] returnTypes,
            final String sqlQuery, final Object[] params, final long limit, final long offset) throws NuxeoException {
        final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.getKey());
        addParam(strBuilder, Long.toString(limit));
        addParam(strBuilder, Long.toString(offset));

        strBuilder.append(FlexibleQueryMaker.KeyCode.SQL_CODE.getKey());
        addParams(strBuilder, returnTypes);
        strBuilder.append(sqlQuery);

        return doFNXQLQuery(session, strBuilder.toString(), params);
    }

    private static void addParam(final StringBuilder strBuilder, final String param) {
        strBuilder.append('[').append(param).append(']');
    }

    private static void addParams(final StringBuilder strBuilder, final String[] params) {
        if (params != null) {
            strBuilder.append('[');
            if (params.length > 0) {
                int i = 0;
                strBuilder.append(params[i]);
                ++i;
                for (i = 1; i < params.length; ++i) {
                    strBuilder.append(",").append(params[i]);
                }
            }
            strBuilder.append(']');
        }
    }

    /**
     * Retrieve a list of document from a NXQL call
     * pagination (limit, offset) is done in the query.
     * 
     * 
     * @param session
     * @param queryNXQL
     * @param limit Limite le nombre de résultats
     * @param offset Offset du nombre de résultats
     * @return
     * @throws NuxeoException
     */
    public static DocumentModelList doQuery(final CoreSession session, final String queryNXQL, final long limit,
            final long offset) throws NuxeoException {
        return doQuery(session, queryNXQL, limit, offset, null);
    }

    /**
     * Retrieve a list of document from a NXQL call
     * pagination (limit, offset) is done in the query.
     * 
     * 
     * @param session
     * @param queryNXQL
     * @param params
     * @param limit Limite le nombre de résultats
     * @param offset Offset du nombre de résultats
     * @return
     * @throws NuxeoException
     */
    public static DocumentModelList doQuery(final CoreSession session, final String queryNXQL, final long limit,
            final long offset, final Object[] params) throws NuxeoException {

        final List<String> ids = doQueryForIds(session, queryNXQL, limit, offset, params);
        final DocumentRef[] refs = new DocumentRef[ids.size()];

        int i = 0;
        for (final String id : ids) {
            refs[i] = new IdRef(id);
            ++i;
        }

        final long t0 = System.currentTimeMillis();

        final DocumentModelList dlist = session.getDocuments(refs);

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Fetching %d document(s) done in %d ms ", refs.length,
                    System.currentTimeMillis() - t0));
        }

        if (dlist.size() != refs.length) {
            LOG.warn("SOME DOCUMENTS HAVE BEEN SKIPPED (PROBABLY FOR PERMISSION REASON)");
        }

        return dlist;
    }

    /**
     * Convert a UFNXQL query to FNXQL
     * 
     * @param queryUFNXQL
     * @return
     */
    public static String ufnxqlToFnxqlQuery(final String queryUFNXQL) {
        return FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + queryUFNXQL;
    }

    /**
     * Exécute une requête UFNXQL.
     * 
     * @param session Session
     * @param queryUFNXQL Requête
     * @param params Paramètres
     * @return Résultats de la requête
     * @throws NuxeoException
     */
    public static IterableQueryResult doUFNXQLQuery(final CoreSession session, final String queryUFNXQL,
            final Object[] params) throws NuxeoException {
        return doFNXQLQuery(session, ufnxqlToFnxqlQuery(queryUFNXQL), params);
    }

    public static <T> List<T> doUFNXQLQueryAndMapping(final CoreSession session, final String queryUFNXQL,
            final Object[] params, final RowMapper<T> mapper) throws NuxeoException {
        return doUFNXQLQueryAndMapping(session, queryUFNXQL, params, -1, -1, mapper);
    }

    public static <T> List<T> doUFNXQLQueryAndMapping(final CoreSession session, final String queryUFNXQL,
            final Object[] params, final long limit, final long offset, final RowMapper<T> mapper)
                    throws NuxeoException {
        final StringBuilder strBuilder = new StringBuilder();
        if (limit > 0) {
            strBuilder.append(FlexibleQueryMaker.KeyCode.PAGE.getKey());
            addParam(strBuilder, Long.toString(limit));
            addParam(strBuilder, Long.toString(offset));
        }
        strBuilder.append(FlexibleQueryMaker.KeyCode.UFXNQL.getKey()).append(queryUFNXQL);

        return doFNXQLQueryAndMapping(session, strBuilder.toString(), params, mapper);
    }

    public static <T> T doUFNXQLQueryAndMapping(final CoreSession session, final String queryUFNXQL,
            final Object[] params, final long limit, final long offset, final Mapper<T> mapper) throws NuxeoException {
        final StringBuilder strBuilder = new StringBuilder();
        if (limit > 0) {
            strBuilder.append(FlexibleQueryMaker.KeyCode.PAGE.getKey());
            addParam(strBuilder, Long.toString(limit));
            addParam(strBuilder, Long.toString(offset));
        }
        strBuilder.append(FlexibleQueryMaker.KeyCode.UFXNQL.getKey()).append(queryUFNXQL);

        return doFNXQLQueryAndMapping(session, strBuilder.toString(), params, mapper);
    }

    /**
     * 
     * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en parametre
     * l'id du document sur lequel tester les ACLS)
     * 
     * @param session
     * @param queryUFNXQL : la requete doit retourner une colone de type uuid sous le label 'id'
     * @param params
     * @param limit
     * @param offset
     * @return
     * @throws NuxeoException
     */
    public static List<String> doUFNXQLQueryForIdsList(final CoreSession session, final String queryUFNXQL,
            final Object[] params, final long limit, final long offset) throws NuxeoException {

        return doQueryForIds(session, ufnxqlToFnxqlQuery(queryUFNXQL), limit, offset, params);
    }

    /**
     * 
     * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en parametre
     * l'id du document sur lequel tester les ACLS)
     * 
     * @param session
     * @param queryUFNXQL : la requete doit retourner une colone de type uuid sous le label 'id'
     * @param params
     * @return
     * @throws NuxeoException
     */
    public static List<String> doUFNXQLQueryForIdsList(final CoreSession session, final String queryUFNXQL,
            final Object[] params) throws NuxeoException {

        return doQueryForIds(session, ufnxqlToFnxqlQuery(queryUFNXQL), 0, 0, params);
    }

    /**
     * 
     * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en parametre
     * l'id du document sur lequel tester les ACLS)
     * 
     * @param session
     * @param queryUFNXQL : la requete doit retourner une colone de type uuid sous le label 'id'
     * @param params
     * @param limit
     * @param offset
     * @return
     * @throws NuxeoException
     */
    public static DocumentRef[] doUFNXQLQueryForIds(final CoreSession session, final String queryUFNXQL,
            final Object[] params, final long limit, final long offset) throws NuxeoException {

        final StringBuilder strBuilder = new StringBuilder();
        if (limit > 0) {
            strBuilder.append(FlexibleQueryMaker.KeyCode.PAGE.getKey());
            addParam(strBuilder, Long.toString(limit));
            addParam(strBuilder, Long.toString(offset));
        }
        strBuilder.append(FlexibleQueryMaker.KeyCode.UFXNQL.getKey()).append(queryUFNXQL);

        return doFNXQLQueryForIds(session, strBuilder.toString(), params);

    }

    public static DocumentRef[] doFNXQLQueryForIds(final CoreSession session, final String queryFNXQL,
            final Object[] params) throws NuxeoException {
        final List<DocumentRef> refs = doFNXQLQueryAndMapping(session, queryFNXQL, params,
                new RowMapper<DocumentRef>() {
                    @Override
                    public DocumentRef doMapping(final Map<String, Serializable> m) throws NuxeoException {
                        final Serializable id = m.get(FlexibleQueryMaker.COL_ID);
                        if (id == null) {
                            throw new NuxeoException(String.format(ERROR_MSG_COL_MISSING_FMT, FlexibleQueryMaker.COL_ID,
                                    FlexibleQueryMaker.COL_ID));
                        }
                        return new IdRef((String) id);
                    }
                });
        return refs.toArray(new DocumentRef[refs.size()]);
    }

    /**
     * 
     * @param session
     * @param queryUFNXQL : la requete doit retourner une colonne de type uuid sous le label 'id'
     * @param params
     * @return
     * @throws NuxeoException
     */
    public static DocumentRef[] doUFNXQLQueryForIds(final CoreSession session, final String queryUFNXQL,
            final Object[] params) throws NuxeoException {
        return doUFNXQLQueryForIds(session, queryUFNXQL, params, 0, 0);
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents sans vérification des ACL.
     * La requête doit retourner une colonne de type uuid sous le label 'id'.
     * 
     * @param session Session (non restreinte)
     * @param queryString Requête UFNXQL
     * @param params Paramètres de le requête
     * @param limit Limite le nombre de résultats
     * @param offset Offset du nombre de résultats
     * @return Liste de documents
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String queryString, final Object[] params, final long limit, final long offset)
                    throws NuxeoException {
        final List<DocumentModel> documentList = new ArrayList<DocumentModel>();
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() throws NuxeoException {
                final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, queryString, params, limit, offset);
                if (refs != null) {
                    for (final DocumentRef ref : refs) {
                        documentList.add(session.getDocument(ref));
                    }
                }
            }
        }.runUnrestricted();
        return documentList;
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents sans vérification des ACL.
     * La requête doit retourner une colonne de type uuid sous le label 'id'.
     * 
     * @param session Session (non restreinte)
     * @param queryString Requête UFNXQL
     * @param params Paramètres de le requête
     * @return Liste de documents
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String queryString, final Object[] params) throws NuxeoException {
        return doUnrestrictedUFNXQLQueryAndFetchForDocuments(session, queryString, params, 0, 0);
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents.
     * La requête doit retourner une colonne de type uuid sous le label 'id'.
     * 
     * @param session Session (non restreinte)
     * @param queryString Requête UFNXQL
     * @param params Paramètres de le requête
     * @param limit Limite le nombre de résultats
     * @param offset Offset du nombre de résultats
     * @return Liste de documents
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String queryString, final Object[] params, final long limit, final long offset)
                    throws NuxeoException {
        final List<DocumentModel> documentList = new ArrayList<DocumentModel>();
        final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, queryString, params, limit, offset);
        if (refs != null) {
            for (final DocumentRef ref : refs) {
                try {
                    documentList.add(session.getDocument(ref));
                } catch (final DocumentSecurityException e) {
                    // permission error
                    LOG.warn("Error during access to document [" + ref + "]", e);
                }
            }
        }
        return documentList;
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents.<br>
     * La requête doit retourner une colonne de type uuid sous le label 'id'.<br>
     * Utilise retrieveDocuments
     * 
     * @param session
     * @param documentType
     * @param queryString
     * @param params
     * @param limit
     * @param offset
     * @return
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String documentType, final String queryString, final Object[] params, final long limit,
            final long offset) throws NuxeoException {
        final List<String> ids = doUFNXQLQueryForIdsList(session, queryString, params, limit, offset);

        final List<DocumentModel> documentList = new ArrayList<DocumentModel>();

        if (ids != null && !ids.isEmpty()) {
            final DocumentModelList list = retrieveDocuments(session, documentType, ids);
            documentList.addAll(list);
            for (final DocumentModel documentModel : list) {
                final int index = ids.indexOf(documentModel.getId());
                documentList.set(index, documentModel);
            }
        }
        return documentList;
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents.
     * La requête doit retourner une colonne de type uuid sous le label 'id'.
     * 
     * @param session Session (non restreinte)
     * @param queryString Requête UFNXQL
     * @param params Paramètres de le requête
     * @return Liste de documents
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String queryString, final Object[] params) throws NuxeoException {
        return doUFNXQLQueryAndFetchForDocuments(session, queryString, params, 0, 0);
    }

    /**
     * Exécute une requête UFNXQL et retourne une liste de documents.<br>
     * La requête doit retourner une colonne de type uuid sous le label 'id'.<br>
     * Utilise retrieveDocuments
     * 
     * @param session
     * @param documentType
     * @param queryString
     * @param params
     * @return
     * @throws NuxeoException
     */
    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
            final String documentType, final String queryString, final Object[] params) throws NuxeoException {
        return doUFNXQLQueryAndFetchForDocuments(session, documentType, queryString, params, 0, 0);
    }

    private static IterableQueryResult doFNXQLQuery(final CoreSession session, final String queryFNXQL,
            final Object[] params) throws NuxeoException {
        return session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE, params);
    }

    private static IterableQueryResult doFNXQLQuery(final CoreSession session, final String queryFNXQL)
            throws NuxeoException {
        return session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE);
    }

    public static <T> T doFNXQLQueryAndMapping(final CoreSession session, final String queryFNXQL,
            final Object[] params, final Mapper<T> mapper) throws NuxeoException {
        if (mapper == null) {
            return null;
        }
        IterableQueryResult res = null;
        try {
            final long t0 = System.currentTimeMillis();

            res = session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE, params);

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("queryAndFetch : \'%s\' done in %d ms ", queryFNXQL,
                        System.currentTimeMillis() - t0));
            }

            return mapper.doMapping(res);
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }

    private static <T> List<T> doFNXQLQueryAndMapping(final CoreSession session, final String queryFNXQL,
            final Object[] params, final RowMapper<T> rowMapper) throws NuxeoException {
        if (rowMapper == null) {
            return null;
        }
        return doFNXQLQueryAndMapping(session, queryFNXQL, params, new ListMapper<T>(rowMapper));
    }

    /**
     * recupere les document en une fois au lieu de un par un
     * Pour cela, une requete NXQL est execute sur les ids des documents souhaités.
     * 
     * Note : ne recupere les documents de type Proxy
     * 
     * @return
     */
    public static DocumentModelList retrieveDocuments(final CoreSession session, final String documentType,
            final Collection<String> ids, final boolean keepIdsOrder) throws NuxeoException {
        return retrieveDocuments(session, documentType, ids, keepIdsOrder, Boolean.FALSE);
    }

    private static DocumentModelList retrieveDocuments(final CoreSession session, final String documentType,
            final Collection<String> ids, final boolean keepIdsOrder, final boolean withProxy) throws NuxeoException {
        if (ids == null || ids.isEmpty()) {
            return new DocumentModelListImpl();
        }

        final List<String> idsList = new ArrayList<String>(ids);
        int startIdx = 0;
        int endIdx;

        final StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(documentType);
        query.append(" WHERE (");
        //resoudre ORA-01795: le nombre maximum d'expressions autorisé dans une liste est de 1000
        while (startIdx < ids.size()) {
            endIdx = ids.size() - startIdx < 1000 ? ids.size() : startIdx + 999;
            query.append(" ecm:uuid IN (" + StringUtil.join(idsList.subList(startIdx, endIdx), ",", "'") + ")");
            startIdx = endIdx;
            if (startIdx < ids.size()) {
                query.append(" OR ");
            }
        }
        query.append(") ");

        if (!withProxy) {
            // remove proxy
            query.append(" AND ecm:isProxy = 0");
        }

        final long t0 = System.currentTimeMillis();

        final DocumentModelList dml = session.query(query.toString());

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("retrieveDocuments for \'%s\' done in %d ms ", query,
                    System.currentTimeMillis() - t0));
        }

        if (keepIdsOrder) {
            // retourne les docs dans l'ordre des ids
            final Map<String, DocumentModel> idsToDoc = new HashMap<String, DocumentModel>();
            for (final DocumentModel doc : dml) {
                idsToDoc.put(doc.getId(), doc);
            }
            final DocumentModelList ordered = new DocumentModelListImpl(dml.size());
            for (final String anId : ids) {
                final DocumentModel doc = idsToDoc.get(anId);
                if (doc != null) {
                    ordered.add(doc);
                }
            }
            return ordered;
        } else {
            return dml;
        }
    }

    /**
     * recupere les document en une fois au lieu de un par un
     * Pour cela, une requete NXQL est execute sur les ids des documents souhaités.
     * ATTENTION : les documents sont retournés dans un ordre aleatoire (aucun lien avec l'ordre des ids dans la collection)
     * 
     * @see #retrieveDocuments(CoreSession, String, Collection, boolean)
     * 
     * @param session
     * @param documentType
     * @param ids
     * @return
     * @throws NuxeoException
     */
    public static DocumentModelList retrieveDocuments(final CoreSession session, final String documentType,
            final Collection<String> ids) throws NuxeoException {
        return retrieveDocuments(session, documentType, ids, false);
    }

    /**
     * recupere les document en une fois au lieu de un par un
     * Pour cela, une requete NXQL est execute sur les ids des documents souhaités.
     * ATTENTION : les documents sont retournés dans un ordre aleatoire (aucun lien avec l'ordre des ids dans la collection)
     * 
     * @see #retrieveDocuments(CoreSession, String, Collection, boolean)
     * 
     * @param session
     * @param documentType
     * @param ids
     * @return
     * @throws NuxeoException
     */
    public static DocumentModelList retrieveDocumentsAndProxy(final CoreSession session, final String documentType,
            final Collection<String> ids) throws NuxeoException {
        return retrieveDocuments(session, documentType, ids, true, true);
    }

    public static DocumentModelList getChildren(final CoreSession session, final String parentId) {
        return getChildren(session, parentId, null);
    }

    public static DocumentModelList getChildren(final CoreSession session, final String parentId,
            final String documentType) {
        String childType = "Document";
        if (StringUtils.isNotBlank(documentType)) {
            childType = documentType;
        }
        final long t0 = System.currentTimeMillis();

        final DocumentModelList result = session
                .query(String.format("SELECT * FROM %s where ecm:parentId = '%s'", childType, parentId));

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("getChildren [\'%s\', \'%s\'] and done in %d ms ", parentId, childType,
                    System.currentTimeMillis() - t0));
        }

        return result;
    }

    /**
     * Classe utilitaire
     */
    private QueryUtils() {
        super();
    }
}
