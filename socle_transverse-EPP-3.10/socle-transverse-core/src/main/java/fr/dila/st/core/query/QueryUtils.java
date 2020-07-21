package fr.dila.st.core.query;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.AbstractSession;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.ecm.core.storage.PartialList;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Execute des requetes en utilisant le langage FNXQL.
 * 
 * Les methodes statique permettent d'executer des requetes en langage NXQL en les encapsulant dans le langage FNXQL qui
 * permet de faire des count(*) et des accès unrestricted en utilisant une session standard.
 * 
 * @author spesnel
 * 
 */
public final class QueryUtils {

	private static final STLogger	LOGGER						= STLogFactory.getLog(QueryUtils.class);
	private static final String		RESULTMAP_SHOULD_CONTAINS	= "Result map should contains '";
	private static final String		PROBABLY_AS					= "' (probably 'AS ";
	private static final String		MISSING						= "' missing)";
	private static final String		LIMIT_SHOULD_NOT_BE_0		= "limit should not be 0";

	public interface RowMapper<T> {
		T doMapping(Map<String, Serializable> rowData) throws ClientException;
	}

	public interface Mapper<T> {
		T doMapping(IterableQueryResult rset) throws ClientException;
	}

	public static class NullMapper implements Mapper<String> {
		/**
		 * Default constructor
		 */
		public NullMapper() {
			// do nothing
		}

		@Override
		public String doMapping(final IterableQueryResult rset) throws ClientException {
			return null;
		}
	}

	/**
	 * Map a result set to a list of objet
	 * 
	 * @author spesnel
	 * 
	 * @param <T>
	 *            type d'objet
	 */
	public static class MapperList<T> implements Mapper<List<T>> {
		private final RowMapper<T>	rowMapper;

		public MapperList(final RowMapper<T> rowMapper) {
			this.rowMapper = rowMapper;
		}

		@Override
		public List<T> doMapping(final IterableQueryResult rset) throws ClientException {
			final List<T> result = new ArrayList<T>();
			final Iterator<Map<String, Serializable>> iterator = rset.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> map = iterator.next();
				result.add(rowMapper.doMapping(map));
			}
			return result;
		}
	}

	/**
	 * Extrait un resultat de comptage
	 * 
	 * Attend une map d'une ligne contenant un champ FlexibleQueryMaker.COL_COUNT
	 * 
	 * @author spesnel
	 */
	public static class CountMapper implements Mapper<Long> {

		/**
		 * Default constructor
		 */
		public CountMapper() {
			// do nothing
		}

		@Override
		public Long doMapping(final IterableQueryResult rset) throws ClientException {
			final Iterator<Map<String, Serializable>> iterator = rset.iterator();
			Long count = -1L;
			if (iterator.hasNext()) {
				final Map<String, Serializable> map = iterator.next();
				final Serializable value = map.get(FlexibleQueryMaker.COL_COUNT);
				if (value == null) {
					throw new ClientException(RESULTMAP_SHOULD_CONTAINS + FlexibleQueryMaker.COL_COUNT + PROBABLY_AS
							+ FlexibleQueryMaker.COL_COUNT + MISSING);
				}
				count = (Long) value;
			}
			return count;
		}
	}

	/**
	 * Utility class
	 */
	private QueryUtils() {
		// do nothing
	}

	/**
	 * Do count query over NXQL Query
	 * 
	 * @param session
	 * @param queryNXQL
	 *            NXQL or FNXQL query
	 * @return the number of data matching the query
	 * @throws ClientException
	 */
	public static Long doCountQuery(final CoreSession session, final String queryNXQL) throws ClientException {
		return doCountQuery(session, queryNXQL, null);
	}

	/**
	 * Do count query over NXQL Query with params
	 * 
	 * @param session
	 * @param queryNXQL
	 *            NXQL or FNXQL query
	 * @return the number of data matching the query
	 * @throws ClientException
	 */
	public static Long doCountQuery(final CoreSession session, final String queryNXQL, final Object[] params)
			throws ClientException {
		String fnxqlQuery = FlexibleQueryMaker.KeyCode.COUNT_CODE.key + queryNXQL;
		final String[] query = fnxqlQuery.toUpperCase().split("ORDER BY");
		if (query.length == 2) {
			final int nbRemove = query[1].length() + 8;
			fnxqlQuery = fnxqlQuery.substring(0, fnxqlQuery.length() - nbRemove);
		}

		return doFNXQLQueryAndMapping(session, fnxqlQuery, params, new CountMapper());
	}

	/**
	 * Do count query over NXQL Query without ACL test
	 * 
	 * @param session
	 * @param queryNXQL
	 * @return the number of data matching the query
	 * @throws ClientException
	 */
	public static Long doUnrestrictedCountQuery(final CoreSession session, final String queryNXQL, final Object[] params)
			throws ClientException {
		final String query = FlexibleQueryMaker.KeyCode.NO_DB_ACL_CHECK_CODE.key
				+ FlexibleQueryMaker.KeyCode.COUNT_CODE.key + queryNXQL;
		// the FNXQL query skip the ACL test
		return doCountQuery(session, query, params);
	}

	/**
	 * Do a queryAndFetch call without ACL test
	 * 
	 * @param session
	 * @param queryNXQL
	 * @return the number of data matching the query
	 * @throws ClientException
	 */
	public static IterableQueryResult doUnrestrictedQueryAndFetch(final CoreSession session, final String queryNXQL)
			throws ClientException {
		final String query = FlexibleQueryMaker.KeyCode.NO_DB_ACL_CHECK_CODE.key + queryNXQL;
		// the FNXQL query skip the ACL test
		return doFNXQLQuery(session, query);
	}

	public static IterableQueryResult doQueryAndFetchPaginated(final CoreSession session, final String queryNXQL,
			final long limit, final long offset) throws ClientException {
		if (limit == 0) {
			throw new IllegalArgumentException(LIMIT_SHOULD_NOT_BE_0);
		}
		final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.key).append('[')
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
	 * @throws ClientException
	 */
	public static IterableQueryResult doQueryAndFetchPaginatedForUFNXQL(final CoreSession session,
			final String queryUFNXQL, final long limit, final long offset, final Object[] params)
			throws ClientException {
		if (limit == 0) {
			throw new IllegalArgumentException(LIMIT_SHOULD_NOT_BE_0);
		}
		final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.key).append('[')
				.append(limit).append(']').append('[').append(offset).append(']')
				.append(FlexibleQueryMaker.KeyCode.UFXNQL.key).append(queryUFNXQL);
		return doFNXQLQuery(session, strBuilder.toString(), params);
	}

	/**
	 * Retrieve document ids without acl check
	 * 
	 * @param session
	 * @param queryNXQL
	 * @param limitVal
	 * @param offset
	 * @param countTotal
	 * @return
	 * @throws ClientException
	 */
	public static PartialList<Serializable> doUnrestrictedQueryForIds(final CoreSession session,
			final String queryNXQL, final long limit, final long offset, final boolean countTotal)
			throws ClientException {
		IterableQueryResult res = null;
		try {
			res = doUnrestrictedQueryAndFetch(session, queryNXQL);
			if (offset > 0) {
				res.skipTo(offset);
			}

			final Iterator<Map<String, Serializable>> iterator = res.iterator();

			long limitVal = limit;
			final List<Serializable> ids = new ArrayList<Serializable>();
			while (iterator.hasNext() && limitVal != 0) {
				final Map<String, Serializable> map = iterator.next();
				final Serializable idDoc = map.get(FlexibleQueryMaker.COL_ID);
				if (idDoc == null) {
					throw new ClientException(RESULTMAP_SHOULD_CONTAINS + FlexibleQueryMaker.COL_ID + PROBABLY_AS
							+ FlexibleQueryMaker.COL_ID + MISSING);
				}
				ids.add(idDoc);
				if (limitVal > 0) {
					--limitVal;
				}
			}

			final long totalSize = res.size();

			return new PartialList<Serializable>(ids, totalSize);
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

	/**
	 * Retrieve document ids with pagination in query. If required add pagination in FNXQL
	 * 
	 * @param session
	 * @param queryNXQL
	 *            NXQL ou FNXQL query
	 * @param limit
	 * @param offset
	 * @param params
	 *            parameter in the query
	 * @return
	 * @throws ClientException
	 */
	public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL, final long limit,
			final long offset, final Object[] params) throws ClientException {
		String query = null;
		if (limit > 0) {
			final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.key);
			addParam(strBuilder, Long.toString(limit));
			addParam(strBuilder, Long.toString(offset));
			strBuilder.append(queryNXQL);
			query = strBuilder.toString();
		} else {
			query = queryNXQL;
		}

		return doFNXQLQueryAndMapping(session, query, params, new RowMapper<String>() {
			@Override
			public String doMapping(final Map<String, Serializable> rowData) throws ClientException {
				final Serializable idDoc = rowData.get(FlexibleQueryMaker.COL_ID);
				if (idDoc == null) {
					throw new ClientException(RESULTMAP_SHOULD_CONTAINS + FlexibleQueryMaker.COL_ID + PROBABLY_AS
							+ FlexibleQueryMaker.COL_ID + MISSING);
				}
				return (String) idDoc;
			}
		});
	}

	/**
	 * Retrieve document ids with pagination in query. If required add pagination in FNXQL
	 * 
	 * @param session
	 * @param queryNXQL
	 *            NXQL ou FNXQL query
	 * @param limit
	 * @param offset
	 * @return
	 * @throws ClientException
	 */
	public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL, final long limit,
			final long offset) throws ClientException {
		return doQueryForIds(session, queryNXQL, limit, offset, null);
	}

	/**
	 * Retrieve document ids with pagination in query
	 * 
	 * @param session
	 * @param queryNXQL
	 *            NXQL ou FNXQL query
	 * @return
	 * @throws ClientException
	 */
	public static List<String> doQueryForIds(final CoreSession session, final String queryNXQL) throws ClientException {
		return doQueryForIds(session, queryNXQL, 0, 0, null);
	}

	/**
	 * Execute une requete SQL
	 * 
	 * @param session
	 * @param returnTypes
	 *            type de retour de la requete SQL, exprime en schema ou mot cle (exemple dc:title,
	 *            FlexibleQueryMaker.COL_COUNT, FlexibleQueryMaker.COL_ID). Utilisé pour le mapping
	 * @param sqlQuery
	 *            la requete SQL
	 * @param params
	 *            Contient les parametres eventuel de la requete SQL (representé dans la requete par un ?)
	 * @return un objet IterableQueryResult à fermer par l'utilisateur
	 * @throws ClientException
	 */
	public static IterableQueryResult doSqlQuery(final CoreSession session, final String[] returnTypes,
			final String sqlQuery, final Object[] params) throws ClientException {
		final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.SQL_CODE.key);
		addParams(strBuilder, returnTypes);
		strBuilder.append(sqlQuery);

		return doFNXQLQuery(session, strBuilder.toString(), params);
	}

	/**
	 * execute une fonction SQL
	 */
	public static void execSqlFunction(final CoreSession session, final String functionCall, final Object[] params)
			throws ClientException {
		final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.SQL_CODE.key);
		addParams(strBuilder, new String[] { FlexibleQueryMaker.COL_ID });
		strBuilder.append("CALL ").append(functionCall);
		doFNXQLQueryAndMapping(session, strBuilder.toString(), params, new NullMapper());
	}

	/**
	 * Execute une requete SQL et rajoute la pagination
	 * 
	 * @param session
	 * @param returnTypes
	 *            type de retour de la requete SQL, exprime en schema ou mot cle (exemple dc:title,
	 *            FlexibleQueryMaker.COL_COUNT, FlexibleQueryMaker.COL_ID). Utilisé pour le mapping
	 * @param sqlQuery
	 *            la requete SQL
	 * @param params
	 *            Contient les parametres eventuel de la requete SQL (representé dans la requete par un ?)
	 * @param limit
	 *            nombre de resultat max
	 * @param offset
	 *            offset à partir duquel renvoyé les resultats
	 * @return un objet IterableQueryResult à fermer par l'utilisateur
	 * @throws ClientException
	 */
	public static IterableQueryResult doSqlQuery(final CoreSession session, final String[] returnTypes,
			final String sqlQuery, final Object[] params, final long limit, final long offset) throws ClientException {
		final StringBuilder strBuilder = new StringBuilder(FlexibleQueryMaker.KeyCode.PAGE.key);
		addParam(strBuilder, Long.toString(limit));
		addParam(strBuilder, Long.toString(offset));

		strBuilder.append(FlexibleQueryMaker.KeyCode.SQL_CODE.key);
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
				int cpt = 0;
				strBuilder.append(params[cpt]);
				++cpt;
				for (cpt = 1; cpt < params.length; ++cpt) {
					strBuilder.append(",").append(params[cpt]);
				}
			}
			strBuilder.append(']');
		}
	}

	/**
	 * Retrieve a list of document from a NXQL call pagination (limit, offset) is done in the query.
	 * 
	 * 
	 * @param session
	 * @param queryNXQL
	 * @param limit
	 *            Limite le nombre de résultats
	 * @param offset
	 *            Offset du nombre de résultats
	 * @return
	 * @throws ClientException
	 */
	public static DocumentModelList doQuery(final CoreSession session, final String queryNXQL, final long limit,
			final long offset) throws ClientException {

		final List<String> ids = doQueryForIds(session, queryNXQL, limit, offset);
		final DocumentRef[] refs = new DocumentRef[ids.size()];

		int cpt = 0;
		for (final String id : ids) {
			refs[cpt] = new IdRef(id);
			++cpt;
		}

		LOGGER.debug(session, STLogEnumImpl.LOG_DEBUG_TEC, "Nb document : after limit,offset : " + refs.length);

		final DocumentModelList dlist = session.getDocuments(refs);

		if (dlist.size() != refs.length) {
			LOGGER.warn(session, STLogEnumImpl.LOG_DEBUG_TEC,
					"SOME DOCUMENTS HAVE BEEN SKIPPED (PROBABLY FOR PERMISSION REASON)");
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
		return FlexibleQueryMaker.KeyCode.UFXNQL.key + queryUFNXQL;
	}

	/**
	 * Exécute une requête UFNXQL.
	 * 
	 * @param session
	 *            Session
	 * @param queryUFNXQL
	 *            Requête
	 * @param params
	 *            Paramètres
	 * @return Résultats de la requête
	 * @throws ClientException
	 */
	public static IterableQueryResult doUFNXQLQuery(final CoreSession session, final String queryUFNXQL,
			final Object[] params) throws ClientException {
		return doFNXQLQuery(session, ufnxqlToFnxqlQuery(queryUFNXQL), params);
	}

	public static <T> List<T> doUFNXQLQueryAndMapping(final CoreSession session, final String queryUFNXQL,
			final Object[] params, final RowMapper<T> mapper) throws ClientException {
		return doUFNXQLQueryAndMapping(session, queryUFNXQL, params, -1, -1, mapper);
	}

	public static <T> List<T> doUFNXQLQueryAndMapping(final CoreSession session, final String queryUFNXQL,
			final Object[] params, final long limit, final long offset, final RowMapper<T> mapper)
			throws ClientException {
		final StringBuilder strBuilder = new StringBuilder();
		if (limit > 0) {
			strBuilder.append(FlexibleQueryMaker.KeyCode.PAGE.key);
			addParam(strBuilder, Long.toString(limit));
			addParam(strBuilder, Long.toString(offset));
		}
		strBuilder.append(FlexibleQueryMaker.KeyCode.UFXNQL.key).append(queryUFNXQL);

		return doFNXQLQueryAndMapping(session, strBuilder.toString(), params, mapper);
	}

	/**
	 * 
	 * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en
	 * parametre l'id du document sur lequel tester les ACLS)
	 * 
	 * @param session
	 * @param queryUFNXQL
	 *            : la requete doit retourner une colone de type uuid sous le label 'id'
	 * @param params
	 * @param limit
	 * @param offset
	 * @return
	 * @throws ClientException
	 */
	public static List<String> doUFNXQLQueryForIdsList(final CoreSession session, final String queryUFNXQL,
			final Object[] params, final long limit, final long offset) throws ClientException {

		return doQueryForIds(session, ufnxqlToFnxqlQuery(queryUFNXQL), limit, offset, params);
	}

	/**
	 * 
	 * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en
	 * parametre l'id du document sur lequel tester les ACLS)
	 * 
	 * @param session
	 * @param queryUFNXQL
	 *            : la requete doit retourner une colone de type uuid sous le label 'id'
	 * @param params
	 * @return
	 * @throws ClientException
	 */
	public static List<String> doUFNXQLQueryForIdsList(final CoreSession session, final String queryUFNXQL,
			final Object[] params) throws ClientException {

		return doQueryForIds(session, ufnxqlToFnxqlQuery(queryUFNXQL), 0, 0, params);
	}

	/**
	 * 
	 * Par defaut les requetes UFNXQL ne testent pas les ACLS (Pour les tester, il faut ajouter une testAcl qui prend en
	 * parametre l'id du document sur lequel tester les ACLS)
	 * 
	 * @param session
	 * @param queryUFNXQL
	 *            : la requete doit retourner une colone de type uuid sous le label 'id'
	 * @param params
	 * @param limit
	 * @param offset
	 * @return
	 * @throws ClientException
	 */
	public static DocumentRef[] doUFNXQLQueryForIds(final CoreSession session, final String queryUFNXQL,
			final Object[] params, final long limit, final long offset) throws ClientException {

		final StringBuilder strBuilder = new StringBuilder();
		if (limit > 0) {
			strBuilder.append(FlexibleQueryMaker.KeyCode.PAGE.key);
			addParam(strBuilder, Long.toString(limit));
			addParam(strBuilder, Long.toString(offset));
		}
		strBuilder.append(FlexibleQueryMaker.KeyCode.UFXNQL.key).append(queryUFNXQL);

		final List<DocumentRef> refs = doFNXQLQueryAndMapping(session, strBuilder.toString(), params,
				new RowMapper<DocumentRef>() {
					@Override
					public DocumentRef doMapping(final Map<String, Serializable> map) throws ClientException {
						final Serializable idDoc = map.get(FlexibleQueryMaker.COL_ID);
						if (idDoc == null) {
							throw new ClientException(RESULTMAP_SHOULD_CONTAINS + FlexibleQueryMaker.COL_ID
									+ PROBABLY_AS + FlexibleQueryMaker.COL_ID + MISSING);
						}
						return new IdRef((String) idDoc);
					}
				});
		return refs.toArray(new DocumentRef[refs.size()]);

	}

	/**
	 * 
	 * @param session
	 * @param queryUFNXQL
	 *            : la requete doit retourner une colonne de type uuid sous le label 'id'
	 * @param params
	 * @return
	 * @throws ClientException
	 */
	public static DocumentRef[] doUFNXQLQueryForIds(final CoreSession session, final String queryUFNXQL,
			final Object[] params) throws ClientException {
		return doUFNXQLQueryForIds(session, queryUFNXQL, params, 0, 0);
	}

	/**
	 * Exécute une requête UFNXQL et retourne une liste de documents sans vérification des ACL. La requête doit
	 * retourner une colonne de type uuid sous le label 'id'.
	 * 
	 * @param session
	 *            Session (non restreinte)
	 * @param queryString
	 *            Requête UFNXQL
	 * @param params
	 *            Paramètres de le requête
	 * @param limit
	 *            Limite le nombre de résultats
	 * @param offset
	 *            Offset du nombre de résultats
	 * @return Liste de documents
	 * @throws ClientException
	 */
	public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(final CoreSession session,
			final String documentType, final String queryString, final Object[] params, final long limit,
			final long offset) throws ClientException {
		final List<DocumentModel> documentList = new ArrayList<DocumentModel>();
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				documentList.addAll(doUFNXQLQueryAndFetchForDocuments(session, documentType, queryString, params,
						limit, offset));
			}
		}.runUnrestricted();
		return documentList;
	}

	/**
	 * Exécute une requête UFNXQL et retourne une liste de documents sans vérification des ACL. La requête doit
	 * retourner une colonne de type uuid sous le label 'id'.
	 * 
	 * @param session
	 *            Session (non restreinte)
	 * @param queryString
	 *            Requête UFNXQL
	 * @param params
	 *            Paramètres de le requête
	 * @return Liste de documents
	 * @throws ClientException
	 */
	public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(final CoreSession session,
			final String documentType, final String queryString, final Object[] params) throws ClientException {
		return doUnrestrictedUFNXQLQueryAndFetchForDocuments(session, documentType, queryString, params, 0, 0);
	}

	/**
	 * Exécute une requête UFNXQL et retourne une liste de documents. La requête doit retourner une colonne de type uuid
	 * sous le label 'id'.
	 * 
	 * @param session
	 *            Session (non restreinte)
	 * @param queryString
	 *            Requête UFNXQL
	 * @param params
	 *            Paramètres de le requête
	 * @param limit
	 *            Limite le nombre de résultats
	 * @param offset
	 *            Offset du nombre de résultats
	 * @return Liste de documents
	 * @throws ClientException
	 */
	public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
			final String documentType, final String queryString, final Object[] params, final long limit,
			final long offset) throws ClientException {
		return doUFNXQLQueryAndFetchForDocuments(session, queryString, params, limit, offset, null);
	}
	
	public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
			final String queryString, final Object[] params, final long limit,
			final long offset, PrefetchInfo prefetchInfo) throws ClientException {
		final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, queryString, params, limit, offset);

		final List<DocumentModel> documentList = new ArrayList<DocumentModel>();

		if (ids != null && !ids.isEmpty()) {
			final List<String> idsSet = new ArrayList<String>();
			for (final String id : ids) {
				// remove duplicate ids
				if (!idsSet.contains(id)) {
					idsSet.add(id);
				}
			}
			final DocumentModelList list = retrieveDocuments(session, idsSet, prefetchInfo);

			orderList(list, documentList, idsSet);

		}
		return documentList;
	}

	/**
	 * Reordonne la liste list par rapport à idSet et la retourne dans documentList
	 * 
	 * @param list
	 *            : list nxql de document
	 * @param documentList
	 *            : liste triée des documents
	 * @param idsSet
	 *            : liste triée d'id de documents
	 */
	private static void orderList(final DocumentModelList list, final List<DocumentModel> documentList,
			final List<String> idsSet) {
		final Map<String, DocumentModel> mapDocs = new HashMap<String, DocumentModel>();
		// Garder l'ordre de la requette
		for (final DocumentModel documentModel : list) {
			mapDocs.put(documentModel.getId(), documentModel);
		}

		for (final String id : idsSet) {
			final DocumentModel doc = mapDocs.get(id);
			if (doc == null) {
				// Si un document n'a pas pu être chargé (pas de droits), il n'apparait pas
				LOGGER.debug(null, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, "id ufnxql '" + id
						+ "' non trouvé dans le nxql...");
			} else {
				documentList.add(doc);
			}
		}
	}

	/**
	 * Exécute une requête UFNXQL et retourne une liste de documents. La requête doit retourner une colonne de type uuid
	 * sous le label 'id'.
	 * 
	 * @param session
	 *            Session (non restreinte)
	 * @param queryString
	 *            Requête UFNXQL
	 * @param params
	 *            Paramètres de le requête
	 * @return Liste de documents
	 * @throws ClientException
	 */
	public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(final CoreSession session,
			final String documentType, final String queryString, final Object[] params) throws ClientException {
		return doUFNXQLQueryAndFetchForDocuments(session, documentType, queryString, params, 0, 0);
	}

	private static IterableQueryResult doFNXQLQuery(final CoreSession session, final String queryFNXQL,
			final Object[] params) throws ClientException {
		return session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE, params);
	}

	private static IterableQueryResult doFNXQLQuery(final CoreSession session, final String queryFNXQL)
			throws ClientException {
		return session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE);
	}

	public static <T> T doFNXQLQueryAndMapping(final CoreSession session, final String queryFNXQL,
			final Object[] params, final Mapper<T> mapper) throws ClientException {
		if (mapper == null) {
			return null;
		}
		IterableQueryResult res = null;
		try {
			res = session.queryAndFetch(queryFNXQL, FlexibleQueryMaker.QUERY_TYPE, params);
			return mapper.doMapping(res);
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

	private static <T> List<T> doFNXQLQueryAndMapping(final CoreSession session, final String queryFNXQL,
			final Object[] params, final RowMapper<T> rowMapper) throws ClientException {
		if (rowMapper == null) {
			return null;
		}
		return doFNXQLQueryAndMapping(session, queryFNXQL, params, new MapperList<T>(rowMapper));
	}

	/**
	 * Retourne une requête complête en UFNXQL à partir d'une clause where. Fait les jointures automatiquement à partir
	 * des noms données aux documents.
	 * 
	 * @param assembler
	 * @param whereClause
	 * @return la requête complête.
	 */

	public static String getFullQuery(final QueryAssembler assembler, final String whereClause) {
		assembler.setWhereClause(whereClause);
		return assembler.getFullQuery();
	}

	/**
	 * Retourne une requête complète en UFNXQL à partir d'une clause where (pour la syntaxe, consulter les contribution
	 * de jointure).
	 * 
	 * @param whereClause
	 * @return la requête complète.
	 */

	public static String getFullQuery(final String whereClause, final String name) {
		final JointureService jointureService = STServiceLocator.getJointureService();
		final QueryAssembler assembler = jointureService.getQueryAssembler(name);
		return getFullQuery(assembler, whereClause);
	}

	/**
	 * recupere les document en une fois au lieu de un par un Pour cela, une requete NXQL est execute sur les ids des
	 * documents souhaités.
	 * 
	 * Note : ne recupere les documents de type Proxy
	 * 
	 * @return
	 */
	public static DocumentModelList retrieveDocuments(final CoreSession session, final String documentType,
			final Collection<String> ids) throws ClientException {
//		if (ids == null || ids.isEmpty()) {
//			return new DocumentModelListImpl();
//		}
//
//		if (ids.size() == 1) {
//			String id = (String) ids.toArray()[0];
//			final IdRef idRef = new IdRef(id);
//			// chargement doc par doc si 1 seul element dans la liste
//			final DocumentModelList docs = new DocumentModelListImpl();
//			try {
//				docs.add(session.getDocument(idRef));
//			} catch (final DocumentSecurityException dse) {
//				// ignore l'erreur de chargement si on a pas les droits
//				LOGGER.debug(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
//						"Tentative de chargement sans les droits nécessaires", dse);
//			} catch (final ClientException ce) {
//				LOGGER.warn(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, ce.getMessage());
//				LOGGER.debug(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, ce.getMessage(), ce);
//			}
//			return docs;
//
//		}
//
//		final List<String> idsList = new ArrayList<String>(ids);
//		String[] tmpArr;
//		int startIdx = 0;
//		int endIdx;
//
//		final StringBuilder query = new StringBuilder("SELECT * FROM ");
//		if (StringUtils.isBlank(documentType)) {
//			query.append("Document");
//		} else {
//			query.append(documentType);
//		}
//
//		query.append(" WHERE (");
//		// resoudre ORA-01795: le nombre maximum d'expressions autorisé dans une liste est de 1000
//		while (startIdx < ids.size()) {
//			endIdx = ids.size() - startIdx < 1000 ? ids.size() : startIdx + 999;
//			tmpArr = new String[endIdx - startIdx];
//			query.append(" ecm:uuid IN ("
//					+ StringUtil.join(idsList.subList(startIdx, endIdx).toArray(tmpArr), ",", "'") + ")");
//			startIdx = endIdx;
//			if (startIdx < ids.size()) {
//				query.append(" OR ");
//			}
//		}
//		query.append(") and ecm:isProxy = 0");
//		return session.query(query.toString());
		return retrieveDocuments(session, ids, null);
	}
	
	public static DocumentModelList retrieveDocuments(final CoreSession session,
			final Collection<String> ids, PrefetchInfo prefetchInfo) throws ClientException {
		if (ids == null || ids.isEmpty()) {
			return new DocumentModelListImpl();
		}
		DocumentModelList list = session.getDocuments(ids, prefetchInfo);
		// vérification des permissions car session.getDocuments est une méthode optimisée qui ne traite
		// pas les vérifications de permission.
		final DocumentModelList docs = new DocumentModelListImpl();
		for (DocumentModel model : list) {
			try {
				if(hasPermissionOverride(session, model.getRef(), SecurityConstants.READ)) {
					docs.add(model);
				}
			} catch (DocumentException e) {
				throw new ClientException("Erreur lors de la vérification des permissions", e);
			}
		}
		return docs;
	}

	/**
	 * Récupère une liste de documents en une fois sans vérification des ACL.
	 * 
	 * @param session
	 * @param documentType
	 * @param ids
	 * @return
	 * @throws ClientException
	 */
	public static List<DocumentModel> retrieveDocumentsUnrestricted(final CoreSession session,
			final String documentType, final Collection<String> ids) throws ClientException {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<DocumentModel>();
		}
		String[] arrayIds = new String[ids.size()];
		arrayIds = ids.toArray(arrayIds);
		final StringBuilder query = new StringBuilder("SELECT d.ecm:uuid AS id FROM ");
		query.append(documentType);
		query.append(" AS d WHERE d.ecm:uuid IN (");
		query.append(StringUtil.join(arrayIds, ",", "'"));
		query.append(")");
		return doUnrestrictedUFNXQLQueryAndFetchForDocuments(session, documentType, query.toString(), new Object[] {});
	}

	/**
	 * 
	 * Méthode pour addresser la limite de 1000 paramètres dans le IN clause d'Oracle
	 * 
	 * @param column
	 *            : le nom de la colonne
	 * @param count
	 *            : le nombre de paramètres désiré
	 * 
	 * @return un String pour completer la requete
	 */
	public static String getQuestionMarkQueryWithColumn(final String column, final int count) {
		if (count <= 0) {
			return "";
		}

		final StringBuilder returnString = new StringBuilder();
		int remaining = count;
		final int loopNumber = (int) Math.ceil((double) count / 1000);
		String partialQuestrionMarks = "";

		for (int l = 0; l < loopNumber; l++) {
			partialQuestrionMarks = StringUtil.getQuestionMark(remaining > 1000 ? 1000 : remaining);
			returnString.append(column).append(" IN (").append(partialQuestrionMarks).append(")");
			remaining -= 1000;
			if (remaining > 0) {
				returnString.append(" OR ");
			}
		}

		return returnString.toString();
	}

	/**
	 * isAdministrator + hasPermission
	 */
	protected final static boolean hasPermissionOverride(CoreSession session, DocumentRef docRef, String permission)
			throws DocumentSecurityException, DocumentException, ClientException {
		if (isAdministrator(session) || session.hasPermission(docRef, permission)) {
			return true;
		}
		return false;
	}

	/**
	 * From {@link AbstractSession}
	 */
	protected static boolean isAdministrator(CoreSession session) {
		Principal principal = session.getPrincipal();
		// FIXME: this is inconsistent with NuxeoPrincipal#isAdministrator
		// method because it allows hardcoded Administrator user
		if (Framework.isTestModeSet()) {
			if (SecurityConstants.ADMINISTRATOR.equals(principal.getName())) {
				return true;
			}
		}
		if (SecurityConstants.SYSTEM_USERNAME.equals(principal.getName())) {
			return true;
		}
		if (principal instanceof NuxeoPrincipal) {
			return ((NuxeoPrincipal) principal).isAdministrator();
		}
		return false;
	}

}
