package fr.dila.st.core.query;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.DEFAULT;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.FAIL_GET_DOCUMENT_TEC;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doCountQuery;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doFNXQLQueryAndMapping;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.ufnxqlToFnxqlQuery;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.nuxeo.ecm.core.query.sql.model.Predicates.in;
import static org.nuxeo.ecm.core.storage.sql.PersistenceContext.SEL_WARN_THRESHOLD_DEFAULT;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.CountMapper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.AbstractSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.model.Repository;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.ecm.core.storage.sql.Invalidations;
import org.nuxeo.ecm.core.storage.sql.Mapper;
import org.nuxeo.ecm.core.storage.sql.Session;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLSession;
import org.nuxeo.runtime.api.Framework;

/**
 * Execute des requetes en utilisant le langage FNXQL.
 *
 * Les methodes statique permettent d'executer des requetes en langage NXQL en les encapsulant dans le langage FNXQL qui
 * permet de faire des count(*) et des accès unrestricted en utilisant une session standard.
 *
 * @author spesnel
 *
 */
public final class QueryHelper {
    private static final STLogger LOGGER = STLogFactory.getLog(QueryHelper.class);

    /**
     * maximum number of expressions in a list for Oracle database
     */
    public static final int MAX_NB_EXPRESSIONS_IN_LIST = 1000;

    public static final long DEFAULT_PAGE_SIZE = 10L;

    public static boolean isFolderEmpty(CoreSession session, String id) {
        return (getFolderSize(session, id) == 0L);
    }

    public static Long getFolderSize(CoreSession session, String id) {
        return doCountQuery(
            session,
            ufnxqlToFnxqlQuery(
                "SELECT d.ecm:uuid as id FROM Document as d WHERE d.ecm:parentId = ? AND testAcl(d.ecm:uuid) = 1"
            ),
            new Object[] { id }
        );
    }

    public static long doUfnxqlCountQuery(CoreSession session, String ufnxqlQuery, Object... params) {
        return doCountQuery(session, ufnxqlToFnxqlQuery(ufnxqlQuery), params);
    }

    public static DocumentModel getDocument(CoreSession session, String id, String... prefetch) {
        return getDocument(session, id, toPrefetchInfo(prefetch));
    }

    public static DocumentModel getDocument(CoreSession session, String id, PrefetchInfo prefetch) {
        DocumentModelList dml = session.getDocuments(singleton(id), prefetch);

        if (dml.isEmpty()) {
            throw new DocumentNotFoundException(id);
        }
        return dml.get(0);
    }

    public static DocumentModelList getDocuments(CoreSession session, Collection<String> ids, String... prefetch) {
        return session.getDocuments(ids, toPrefetchInfo(prefetch));
    }

    public static PrefetchInfo toPrefetchInfo(String... prefetch) {
        return new PrefetchInfo(StringUtils.join(prefetch, ","));
    }

    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(
        final CoreSession session,
        final String queryString,
        final Object[] params,
        final long limit,
        final long offset,
        PrefetchInfo prefetchInfo
    ) {
        final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, queryString, params, limit, offset);

        final List<DocumentModel> documentList = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            final List<String> idsSet = new ArrayList<>();
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

    public static List<DocumentModel> doUFNXQLQueryAndFetchForDocuments(
        final CoreSession session,
        final String queryString,
        final Object[] params,
        final long limit,
        final long offset,
        String... prefetch
    ) {
        return doUFNXQLQueryAndFetchForDocuments(session, queryString, params, limit, offset, toPrefetchInfo(prefetch));
    }

    public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(
        final CoreSession session,
        final String queryString,
        final Object[] params,
        final long limit,
        final long offset,
        PrefetchInfo prefetchInfo
    ) {
        return CoreInstance.doPrivileged(
            session,
            systemSession -> {
                return doUFNXQLQueryAndFetchForDocuments(
                    systemSession,
                    queryString,
                    params,
                    limit,
                    offset,
                    prefetchInfo
                );
            }
        );
    }

    public static List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(
        final CoreSession session,
        final String queryString,
        final Object[] params,
        final long limit,
        final long offset,
        String... prefetch
    ) {
        return doUnrestrictedUFNXQLQueryAndFetchForDocuments(
            session,
            queryString,
            params,
            limit,
            offset,
            toPrefetchInfo(prefetch)
        );
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
    private static void orderList(
        final DocumentModelList list,
        final List<DocumentModel> documentList,
        final List<String> idsSet
    ) {
        final Map<String, DocumentModel> mapDocs = new HashMap<>();
        // Garder l'ordre de la requette
        for (final DocumentModel documentModel : list) {
            mapDocs.put(documentModel.getId(), documentModel);
        }

        for (final String id : idsSet) {
            final DocumentModel doc = mapDocs.get(id);
            if (doc == null) {
                // Si un document n'a pas pu être chargé (pas de droits), il n'apparait pas
                LOGGER.debug(
                    null,
                    STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
                    "id ufnxql '" + id + "' non trouvé dans le nxql..."
                );
            } else {
                documentList.add(doc);
            }
        }
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

    public static DocumentModelList retrieveDocuments(
        final CoreSession session,
        final Collection<String> ids,
        PrefetchInfo prefetchInfo
    ) {
        if (ids == null || ids.isEmpty()) {
            return new DocumentModelListImpl();
        }

        DocumentModelList list = session.getDocuments(ids, prefetchInfo);

        // vérification des permissions car session.getDocuments est une méthode optimisée qui ne traite
        // pas les vérifications de permission.
        final DocumentModelList docs = new DocumentModelListImpl();
        for (DocumentModel model : list) {
            try {
                if (hasPermissionOverride(session, model.getRef(), SecurityConstants.READ)) {
                    docs.add(model);
                }
            } catch (NuxeoException e) {
                throw new NuxeoException("Erreur lors de la vérification des permissions", e);
            }
        }
        return docs;
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
            partialQuestrionMarks = StringHelper.getQuestionMark(remaining > 1000 ? 1000 : remaining);
            returnString.append(column).append(" IN (").append(partialQuestrionMarks).append(")");
            remaining -= 1000;
            if (remaining > 0) {
                returnString.append(" OR ");
            }
        }

        return returnString.toString();
    }

    public static Long doCountDistinctQuery(final CoreSession session, final String queryUFNXQL, Collection<?> params) {
        final Object[] paramsArray = params.toArray(new Object[params.size()]);
        return doCountDistinctQuery(session, queryUFNXQL, paramsArray);
    }

    public static Long doCountDistinctQuery(final CoreSession session, final String queryUFNXQL, Object[] params) {
        return doFNXQLQueryAndMapping(session, ufnxqlToFnxqlQuery(queryUFNXQL), params, new CountMapper());
    }

    /**
     * isAdministrator + hasPermission
     */
    private static boolean hasPermissionOverride(CoreSession session, DocumentRef docRef, String permission) {
        return isAdministrator(session) || session.hasPermission(docRef, permission);
    }

    /**
     * From {@link AbstractSession}
     */
    private static boolean isAdministrator(CoreSession session) {
        NuxeoPrincipal principal = session.getPrincipal();
        if (SecurityConstants.SYSTEM_USERNAME.equals(principal.getName())) {
            return true;
        }
        return principal.isAdministrator();
    }

    /**
     * Applique la fonction de query par tranche de 1000 ids
     * <p>
     * resoudre ORA-01795: le nombre maximum d'expressions autorisé dans une liste est de 1000
     *
     * @param docIds ids de documents
     * @param property lvalue de l'expression
     * @param queryFunction la fonction de query
     * @return la liste de documentModel
     */
    public static List<DocumentModel> getDocsFromIds(
        Collection<String> docIds,
        String property,
        Function<Predicate, List<DocumentModel>> queryFunction
    ) {
        Objects.requireNonNull(docIds);

        return ListUtils
            .partition(new LinkedList<>(docIds), MAX_NB_EXPRESSIONS_IN_LIST)
            .stream()
            .map(ids -> queryFunction.apply(in(property, ids)))
            .flatMap(List::stream)
            .collect(toList());
    }

    public static void invalidateAllCache(CoreSession coreSession) {
        RepositoryService repositoryService = Framework.getService(RepositoryService.class);
        Repository repository = repositoryService.getRepository(coreSession.getRepositoryName());
        SQLSession sqlSession = (SQLSession) repository.getSession();
        Field field;
        try {
            field = SQLSession.class.getDeclaredField("session");
            field.setAccessible(true);
            Session session = (Session) field.get(sqlSession);
            Mapper mapper = session.getMapper();
            Invalidations invalidations = new Invalidations();
            invalidations.all = true;
            mapper.sendInvalidations(invalidations);
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new NuxeoException(e);
        }
    }

    public static boolean checkCount(CoreSession session, long count, Object... objs) {
        if (count > Long.valueOf(SEL_WARN_THRESHOLD_DEFAULT)) {
            LOGGER.error(
                session,
                FAIL_GET_DOCUMENT_TEC,
                new NuxeoException(
                    "Le nombre de résultats qu'on veut récupérer est supérieur à " +
                    SEL_WARN_THRESHOLD_DEFAULT +
                    " et fera saturer la mémoire du serveur. Il faut que la requête soit paginée." +
                    " On retourne " +
                    DEFAULT_PAGE_SIZE +
                    " résultats."
                )
            );
            if (ArrayUtils.isNotEmpty(objs)) {
                Arrays
                    .asList(objs)
                    .forEach(
                        obj -> {
                            LOGGER.info(DEFAULT, obj.toString());
                        }
                    );
            }
            return false;
        }

        return true;
    }

    private QueryHelper() {}
}
