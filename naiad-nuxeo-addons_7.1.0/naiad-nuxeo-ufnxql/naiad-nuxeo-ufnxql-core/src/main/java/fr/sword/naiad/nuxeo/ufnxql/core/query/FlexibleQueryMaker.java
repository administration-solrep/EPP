package fr.sword.naiad.nuxeo.ufnxql.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.ModelProperty;
import org.nuxeo.ecm.core.storage.sql.Session.PathResolver;
import org.nuxeo.ecm.core.storage.sql.jdbc.NXQLQueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.ColumnMapMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectSQLServer;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLQueryMaker;

/**
 * Enable to do count query over NXQL query or skip NX_ACCESS_ALLOWED test in
 * query
 * 
 * Use NXQLQueryMaker to generate the SQL query. And then encapsule it in a
 * 'select count(*) from (xxxx);' to retrieve the count of element
 * 
 * Format des requetes FNXQL : (&lt;mot cle&gt;[&lt;arg&gt;]*)*&lt;query&gt; Exemple :
 * 
 * "count:select * from Dossier"
 * "sql:[dc:title,count]select title, count(*) from dublincore group by title"
 * 
 * @author spesnel
 * 
 */
public class FlexibleQueryMaker implements QueryMaker {

    /**
     * mot cle pour identifier le type de language
     */
    public static final String QUERY_TYPE = "FNXQL";
    public static final String NXQL_QUERY_TYPE = "NXQL";

    public static final String COL_ID = "id";
    public static final String COL_COUNT = "count";
    public static final String COL_PARENT_ID = "parentId";

    /**
     * Identifie le type de colonne spécifique "owner" pour la table LOCK
     */
    public static final String COL_LOCK_OWNER = "owner";

    private static final Log LOG = LogFactory.getLog(FlexibleQueryMaker.class);

    public enum KeyCode {

        NO_DB_ACL_CHECK_CODE("noacl:"),
        COUNT_CODE("count:"),
        SQL_CODE("sql:"),
        SQL_MIX_CODE("sqlmix:"),
        PAGE("page:"),
        UFXNQL("ufnxql:");

        private String key;

        private KeyCode(final String key) {
            this.key = key;
        }

        public boolean match(final String query) {
            return query != null && query.startsWith(key);
        }

        public String getKey() {
            return key;
        }
    }

    public FlexibleQueryMaker() {
        // do nothing
    }

    @Override
    public boolean accepts(final String type) {
        return QUERY_TYPE.equals(type) || NXQL_QUERY_TYPE.equals(type);
    }

    @Override
    public Query buildQuery(final SQLInfo sqlInfo, final Model model, final PathResolver pathResolver,
            final String query, final QueryFilter queryFilter, final Object... params) { // throws StorageException {

        final FlexibleQueryInfos infos = new FlexibleQueryInfos(query);

        Query q = null;

        QueryFilter queryFilterToUse = queryFilter;

        if (infos.hasKeyCode(KeyCode.NO_DB_ACL_CHECK_CODE)) {
            queryFilterToUse = new QueryFilter(queryFilter.getPrincipal(), null, queryFilter.getPermissions(), null,
                    queryFilter.getQueryTransformers(), 0, 0);
        }

        if (infos.hasKeyCode(KeyCode.SQL_CODE)) {

            if (infos.hasKeyCode(KeyCode.SQL_MIX_CODE)) {
                throw new QueryParseException("INCOMPATIBLE TYPE : " + KeyCode.SQL_CODE + " and "
                        + KeyCode.SQL_MIX_CODE);
            }

            final List<String> args = infos.getArgs(KeyCode.SQL_CODE);
            if (args == null || args.size() != 1) {
                throw new QueryParseException("Unexpected args number for " + KeyCode.SQL_CODE + " "
                        + (args == null ? 0 : args.size()) + " instead of 1");
            }
            final String[] colnames = args.get(0).split(",");

            q = new Query();

            final List<Serializable> paramList = new ArrayList<Serializable>();
            if (params != null) {
                for (int i = 0; i < params.length; ++i) {
                    if (params[i] instanceof Serializable) {
                        paramList.add((Serializable) params[i]);
                    } else {
                        throw new QueryParseException("PARAMS NOT SERIALIZABLE [" + i + "]:" + params[i].toString());
                    }
                }
            }

            q.selectParams = paramList;

            final String sql = infos.getQueryWithoutCode();

            final List<Column> whatColumns = new ArrayList<Column>();
            final List<String> whatKeys = new ArrayList<String>();
            for (final String colname : colnames) {
                final String name = colname.trim();
                final Column column = retrieveColumn(sqlInfo, model, name);

                whatColumns.add(column);
                whatKeys.add(name);
            }

            final ColumnMapMaker mapMaker = new ColumnMapMaker(whatColumns, whatKeys);
            q.selectInfo = new SQLInfoSelect(sql, mapMaker);

        } else if (infos.hasKeyCode(KeyCode.SQL_MIX_CODE)) {

            if (infos.hasKeyCode(KeyCode.SQL_CODE)) {
                throw new QueryParseException("INCOMPATIBLE TYPE : " + KeyCode.SQL_CODE + " and "
                        + KeyCode.SQL_MIX_CODE);
            }

            final List<String> args = infos.getArgs(KeyCode.SQL_MIX_CODE);
            if (args == null || args.size() < 1 || args.size() > 2) {
                throw new QueryParseException("Unexpected args number for " + KeyCode.SQL_MIX_CODE + " "
                        + (args == null ? 0 : args.size()) + " instead of 1 or 2");
            }
            final String[] colnames = args.get(0).split(",");
            int[] paramNumber = null;
            int paramTotal = 0;
            if (args.size() == 2) {
                final String[] paramNumberStr = args.get(1).split(",");
                paramNumber = new int[paramNumberStr.length];
                for (int i = 0; i < paramNumberStr.length; ++i) {
                    paramNumber[i] = Integer.parseInt(paramNumberStr[i]);
                    paramTotal += paramNumber[i];
                }
            }

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    if (!(params[i] instanceof Serializable)) {
                        throw new QueryParseException("PARAMS NOT SERIALIZABLE [" + i + "]:" + params[i].toString());
                    }
                }
                if (paramNumber == null) {
                    throw new QueryParseException("params supported for SQL_MIX only if paramNumber defined");
                }
                if (paramTotal != params.length) {
                    throw new QueryParseException("param number total mismatch the parameter given : total = "
                            + paramTotal + " given params = " + params.length);
                }

            }

            q = new Query();
            q.selectParams = new ArrayList<Serializable>();

            String sql = infos.getQueryWithoutCode();

            int indexPart = 0;
            int indexParams = 0;
            String toAnalyse = sql;
            final StringBuilder strBuilder = new StringBuilder();
            while (toAnalyse.length() > 0) {
                final int idxStart = toAnalyse.indexOf('{');
                if (idxStart < 0) {
                    strBuilder.append(toAnalyse);
                    toAnalyse = "";
                    break;
                }
                final int idxEnd = toAnalyse.indexOf('}');
                if (idxEnd < 0) {
                    throw new QueryParseException("{ not closed");
                }

                final String start = toAnalyse.substring(0, idxStart);
                final String end = toAnalyse.substring(idxEnd + 1);
                final String part = toAnalyse.substring(idxStart + 1, idxEnd);

                Object[] subpartParams = null;
                if (paramNumber != null) {
                    if (indexPart >= paramNumber.length) {
                        throw new QueryParseException(
                                "Too many subquery compared to the number of paramNumber defined ["
                                        + paramNumber.length + "]");
                    }
                    subpartParams = new Object[paramNumber[indexPart]];
                    for (int i = 0; i < subpartParams.length; ++i) {
                        subpartParams[i] = params[indexParams];
                        ++indexParams;
                    }
                }

                final Query tmpq = this.buildQuery(sqlInfo, model, pathResolver, part, queryFilterToUse, subpartParams);

                q.selectParams.addAll(tmpq.selectParams);
                strBuilder.append(start).append(tmpq.selectInfo.sql);
                toAnalyse = end;
                ++indexPart;
            }
            sql = strBuilder.toString();

            final List<Column> whatColumns = new ArrayList<Column>();
            final List<String> whatKeys = new ArrayList<String>();
            for (final String colname : colnames) {
                final String name = colname.trim();
                final Column column = retrieveColumn(sqlInfo, model, name);

                whatColumns.add(column);
                whatKeys.add(name);
            }

            final ColumnMapMaker mapMaker = new ColumnMapMaker(whatColumns, whatKeys);
            q.selectInfo = new SQLInfoSelect(sql, mapMaker);

        } else if (infos.hasKeyCode(KeyCode.UFXNQL)) {
            final UFNXQLQueryMaker maker = new UFNXQLQueryMaker();
            // Désactive l'optimisation des acls
            q = maker.buildQuery(sqlInfo, model, pathResolver, infos.getQueryWithoutCode(), queryFilterToUse, params);
        } else {
            // build the query from the NXQL query part
            final NXQLQueryMaker maker = new NXQLQueryMaker();
            q = maker.buildQuery(sqlInfo, model, pathResolver, infos.getQueryWithoutCode(), queryFilterToUse, params);
        }

        if (infos.hasKeyCode(KeyCode.PAGE)) {
            final List<String> args = infos.getArgs(KeyCode.PAGE);
            if (args.size() != 2) {
                throw new QueryParseException("Unexpected number of arg for PAGE");
            }
            final Long limit = Long.valueOf(args.get(0));
            Long offset = Long.valueOf(args.get(1));

            if (sqlInfo.dialect instanceof DialectOracle) {
                final long end = offset + limit;
                if (offset < 0) {
                    offset = 0L;
                }
                // SPL
                final StringBuilder strBuffer = new StringBuilder("select * from ( select TTSUBQ.* , rownum r FROM (")
                        .append(q.selectInfo.sql).append(") TTSUBQ ) where r > ? AND r <= ?");
                q.selectInfo = new SQLInfoSelect(strBuffer.toString(), q.selectInfo.mapMaker);

                q.selectParams.add(offset);
                q.selectParams.add(end);

            } else if (sqlInfo.dialect instanceof DialectSQLServer) {
                final long end = offset + limit;
                final StringBuilder strBuffer = new StringBuilder("SELECT *  FROM ( ");
                strBuffer.append(" SELECT ROW_NUMBER() OVER(ORDER BY [id]) AS row, t1.* ");
                strBuffer.append(" FROM (");
                strBuffer.append(q.selectInfo.sql);
                strBuffer.append(" ) t1 ");
                strBuffer.append(" ) t2 ");
                strBuffer.append(" WHERE t2.row BETWEEN ");
                strBuffer.append(offset + 1);
                strBuffer.append(" AND ");
                strBuffer.append(end);
                // no offset limit
            } else {
                final StringBuilder strBuffer = new StringBuilder()// "select * from ( select TTSUBQ.* , rownum r FROM (")
                        .append(q.selectInfo.sql).append(" LIMIT ? OFFSET ?");
                q.selectInfo = new SQLInfoSelect(strBuffer.toString(), q.selectInfo.mapMaker);

                q.selectParams.add(limit);
                q.selectParams.add(offset);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("PAGE[" + q.selectInfo.sql + "]");
            }
        }

        if (infos.hasKeyCode(KeyCode.COUNT_CODE)) {
            final StringBuilder sqlBuffer = new StringBuilder("select count(*) from (").append(q.selectInfo.sql)
                    .append(") ");
            if (!(sqlInfo.dialect instanceof DialectOracle)) {
                sqlBuffer.append(" AS subquery");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("COUNT[" + sqlBuffer + "]");
            }
            q.selectInfo = new SQLInfoSelect(sqlBuffer.toString(), ColumnUtil.createMapMakerForCount(sqlInfo.dialect,
                    COL_COUNT));
        }

        return q;
    }

    @Override
    public String getName() {
        return QUERY_TYPE;
    }

    private Column retrieveColumn(final SQLInfo sqlInfo, final Model model, final String name) {
        Column column = null;
        final ModelProperty propertyInfo = model.getPropertyInfo(name);
        if (propertyInfo != null) {
            final Database database = sqlInfo.database;
            final Table table = database.getTable(propertyInfo.fragmentName);
            column = table.getColumn(propertyInfo.fragmentKey);
        }

        if (column == null) {
            column = retrieveParticularColumn(sqlInfo, name);
        }

        if (column == null) {
            throw new QueryParseException("Unknown field: " + name);
        } else {
            return column;
        }
    }

    private Column retrieveParticularColumn(final SQLInfo sqlInfo, final String name) {
        if (COL_COUNT.equals(name)) {
            return ColumnUtil.createCountColumn(sqlInfo.dialect);
        } else if (COL_ID.equals(name)) {
            final Database database = sqlInfo.database;
            final Table hier = database.getTable(Model.HIER_TABLE_NAME);
            return hier.getColumn(Model.MAIN_KEY);
        } else if (COL_LOCK_OWNER.equals(name)) {
            final Database database = sqlInfo.database;
            final Table hier = database.getTable(Model.LOCK_TABLE_NAME);
            return hier.getColumn(Model.LOCK_OWNER_KEY);
        } else {
            return null;
        }
    }

}
