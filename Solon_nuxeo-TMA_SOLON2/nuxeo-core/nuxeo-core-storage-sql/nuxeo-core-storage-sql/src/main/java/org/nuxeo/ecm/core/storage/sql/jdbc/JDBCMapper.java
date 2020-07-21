/*
* Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Florent Guillaume
*/
package org.nuxeo.ecm.core.storage.sql.jdbc;

import java.io.Serializable;
import java.security.MessageDigest;
import java.sql.Array;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.storage.ConnectionResetException;
import org.nuxeo.ecm.core.storage.PartialList;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.Invalidations;
import org.nuxeo.ecm.core.storage.sql.LockManager;
import org.nuxeo.ecm.core.storage.sql.Mapper;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.Row;
import org.nuxeo.ecm.core.storage.sql.RowId;
import org.nuxeo.ecm.core.storage.sql.Session.PathResolver;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;
import org.nuxeo.runtime.api.Framework;

/**
* A {@link JDBCMapper} maps objects to and from a JDBC database. It is specific
* to a given database connection, as it computes statements.
* <p>
* The {@link JDBCMapper} does the mapping according to the policy defined by a
* {@link Model}, and generates SQL statements recorded in the {@link SQLInfo}.
*/
public class JDBCMapper extends JDBCRowMapper implements Mapper {

    private static final Log log = LogFactory.getLog(JDBCMapper.class);

    public static Map<String, Serializable> testProps = new HashMap<String, Serializable>();

    public static final String TEST_UPGRADE = "testUpgrade";

    // property in sql.txt file
    public static final String TEST_UPGRADE_VERSIONS = "testUpgradeVersions";

    public static final String TEST_UPGRADE_LAST_CONTRIBUTOR = "testUpgradeLastContributor";

    public static final String TEST_UPGRADE_LOCKS = "testUpgradeLocks";

    protected TableUpgrader tableUpgrader;

    private final QueryMakerService queryMakerService;

    private final PathResolver pathResolver;

    /**
    * Creates a new Mapper.
    *
    * @param model the model
    * @param pathResolver the path resolver (used for startswith queries)
    * @param sqlInfo the sql info
    * @param xadatasource the XA datasource to use to get connections
    * @param clusterNodeHandler the cluster node handler
    * @param connectionPropagator the connection propagator
    */
    public JDBCMapper(final Model model, final PathResolver pathResolver, final SQLInfo sqlInfo, final XADataSource xadatasource,
            final ClusterNodeHandler clusterNodeHandler, final JDBCConnectionPropagator connectionPropagator) throws StorageException {
        super(model, sqlInfo, xadatasource, clusterNodeHandler, connectionPropagator);
        this.pathResolver = pathResolver;
        try {
            queryMakerService = Framework.getService(QueryMakerService.class);
        } catch (final Exception e) {
            throw new StorageException(e);
        }

        tableUpgrader = new TableUpgrader(this);
        tableUpgrader.add(model.VERSION_TABLE_NAME, model.VERSION_IS_LATEST_KEY, "upgradeVersions", TEST_UPGRADE_VERSIONS);
        tableUpgrader.add("dublincore", "lastContributor", "upgradeLastContributor", TEST_UPGRADE_LAST_CONTRIBUTOR);
        tableUpgrader.add(model.LOCK_TABLE_NAME, model.LOCK_OWNER_KEY, "upgradeLocks", TEST_UPGRADE_LOCKS);
    }

    @Override
    public int getTableSize(final String tableName) {
        return sqlInfo.getDatabase().getTable(tableName).getColumns().size();
    }

    /*
    * ----- Root -----
    */

    @Override
    public void createDatabase() throws StorageException {
        try {
            createTables();
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException(e);
        }
    }

    protected String getTableName(final String origName) {

        if (sqlInfo.dialect instanceof DialectOracle) {
            if (origName.length() > 30) {

                final StringBuilder sb = new StringBuilder(origName.length());

                try {
                    final MessageDigest digest = MessageDigest.getInstance("MD5");
                    sb.append(origName.substring(0, 15));
                    sb.append('_');

                    digest.update(origName.getBytes());
                    sb.append(Dialect.toHexString(digest.digest()).substring(0, 12));

                    return sb.toString();

                } catch (final Exception e) {
                    throw new RuntimeException("Error", e);
                }
            }
        }

        return origName;
    }

    protected void createTables() throws SQLException {
        sqlInfo.executeSQLStatements("beforeTableCreation", this);
        if (testProps.containsKey(TEST_UPGRADE)) {
            // create "old" tables
            sqlInfo.executeSQLStatements("testUpgrade", this);
        }

        final String schemaName = sqlInfo.dialect.getConnectionSchema(connection);
        final DatabaseMetaData metadata = connection.getMetaData();
        final Set<String> tableNames = findTableNames(metadata, schemaName);
        final Database database = sqlInfo.getDatabase();
        final Map<String, List<Column>> added = new HashMap<String, List<Column>>();

        final Statement st = connection.createStatement();

        for (final Table table : database.getTables()) {
            final String tableName = getTableName(table.getPhysicalName());
            if (tableNames.contains(tableName.toUpperCase())) {
                sqlInfo.dialect.existingTableDetected(connection, table, model, sqlInfo.database);
            } else {

                /*
                * Create missing table.
                */

                final boolean create = sqlInfo.dialect.preCreateTable(connection, table, model, sqlInfo.database);
                if (!create) {
                    log.warn("Creation skipped for table: " + tableName);
                    continue;
                }

                final String sql = table.getCreateSql();
                logger.log(sql);
                try {
                    st.execute(sql);
                } catch (final SQLException e) {
                    throw new SQLException("Error creating table: " + sql + " : " + e.getMessage(), e);
                }
                for (final String s : table.getPostCreateSqls(model)) {
                    logger.log(s);
                    try {
                        st.execute(s);
                    } catch (final SQLException e) {
                        throw new SQLException("Error post creating table: " + s + " : " + e.getMessage(), e);
                    }
                }
                for (final String s : sqlInfo.dialect.getPostCreateTableSqls(table, model, sqlInfo.database)) {
                    logger.log(s);
                    try {
                        st.execute(s);
                    } catch (final SQLException e) {
                        throw new SQLException("Error post creating table: " + s + " : " + e.getMessage(), e);
                    }
                }
                added.put(table.getKey(), null); // null = table created
            }

            /*
            * Get existing columns.
            */

            final ResultSet rs = metadata.getColumns(null, schemaName, tableName, "%");
            final Map<String, Integer> columnTypes = new HashMap<String, Integer>();
            final Map<String, String> columnTypeNames = new HashMap<String, String>();
            final Map<String, Integer> columnTypeSizes = new HashMap<String, Integer>();
            while (rs.next()) {
                final String schema = rs.getString("TABLE_SCHEM");
                if (schema != null) { // null for MySQL, doh!
                    if ("INFORMATION_SCHEMA".equals(schema.toUpperCase())) {
                        // H2 returns some system tables (locks)
                        continue;
                    }
                }
                final String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                columnTypes.put(columnName, Integer.valueOf(rs.getInt("DATA_TYPE")));
                columnTypeNames.put(columnName, rs.getString("TYPE_NAME"));
                columnTypeSizes.put(columnName, Integer.valueOf(rs.getInt("COLUMN_SIZE")));
            }

            /*
            * Update types and create missing columns.
            */

            final List<Column> addedColumns = new LinkedList<Column>();
            for (final Column column : table.getColumns()) {
                final String upperName = column.getPhysicalName().toUpperCase();
                final Integer type = columnTypes.remove(upperName);
                if (type == null) {
                    log.warn("Adding missing column in database: " + column.getFullQuotedName());
                    final String sql = table.getAddColumnSql(column);
                    logger.log(sql);
                    try {
                        st.execute(sql);
                    } catch (final SQLException e) {
                        throw new SQLException("Error adding column: " + sql + " : " + e.getMessage(), e);
                    }
                    for (final String s : table.getPostAddSqls(column, model)) {
                        logger.log(s);
                        try {
                            st.execute(s);
                        } catch (final SQLException e) {
                            throw new SQLException("Error post adding column: " + s + " : " + e.getMessage(), e);
                        }
                    }
                    addedColumns.add(column);
                } else {
                    final int expected = column.getJdbcType();
                    final int actual = type.intValue();
                    final String actualName = columnTypeNames.get(upperName);
                    final Integer actualSize = columnTypeSizes.get(upperName);
                    if (!column.setJdbcType(actual, actualName, actualSize.intValue())) {
                        log.error(String.format("SQL type mismatch for %s: expected %s, database has %s / %s (%s)", column.getFullQuotedName(),
                                Integer.valueOf(expected), type, actualName, actualSize));
                    }
                }
            }
            if (!columnTypes.isEmpty()) {
                log.warn("Database contains additional unused columns for table " + table.getQuotedName() + ": "
                        + StringUtils.join(new ArrayList<String>(columnTypes.keySet()), ", "));
            }
            if (!addedColumns.isEmpty()) {
                if (added.containsKey(table.getKey())) {
                    throw new AssertionError();
                }
                added.put(table.getKey(), addedColumns);
            }
        }

        closeStatement(st);

        if (testProps.containsKey(TEST_UPGRADE)) {
            // create "old" content in tables
            sqlInfo.executeSQLStatements("testUpgradeOldTables", this);
        }

        // run upgrade for each table if added columns or test
        for (final Entry<String, List<Column>> en : added.entrySet()) {
            final List<Column> addedColumns = en.getValue();
            final String tableKey = en.getKey();
            upgradeTable(tableKey, addedColumns);
        }
        sqlInfo.executeSQLStatements("afterTableCreation", this);
        sqlInfo.dialect.performAdditionalStatements(connection);
    }

    protected void upgradeTable(final String tableKey, final List<Column> addedColumns) throws SQLException {
        tableUpgrader.upgrade(tableKey, addedColumns);
    }

    /** Finds uppercase table names. */
    protected static Set<String> findTableNames(final DatabaseMetaData metadata, final String schemaName) throws SQLException {
        final Set<String> tableNames = new HashSet<String>();
        final ResultSet rs = metadata.getTables(null, schemaName, "%", new String[] { "TABLE" });
        while (rs.next()) {
            final String tableName = rs.getString("TABLE_NAME");
            tableNames.add(tableName.toUpperCase());
        }
        return tableNames;
    }

    @Override
    public String createClusterNode() throws StorageException {
        Statement st = null;
        try {
         // get the cluster node id from the database, if necessary
            String sql = sqlInfo.dialect.getClusterNodeIdSql();
            String nodeId;
            if (sql == null) {
                nodeId = null;
            } else {
                st = connection.createStatement();
                if (logger.isLogEnabled()) {
                    logger.log(sql);
                }
                ResultSet rs = st.executeQuery(sql);
                if (!rs.next()) {
                    throw new StorageException("Cannot get cluster node id");
                }
                nodeId = rs.getString(1);
                if (logger.isLogEnabled()) {
                    logger.log("  -> cluster node id: " + nodeId);
                }
            }
            // add the cluster node
            sqlInfo.executeSQLStatements("addClusterNode", this);
            return nodeId;
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException(e);
        } finally {
            if (st != null) {
                try {
                    closeStatement(st);
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void removeClusterNode() throws StorageException {
        try {
            sqlInfo.executeSQLStatements("removeClusterNode", this);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException(e);
        }
    }

    @Override
    public void insertClusterInvalidations(final Invalidations invalidations, final String nodeId) throws StorageException {
        String sql = sqlInfo.dialect.getClusterInsertInvalidations();
        if (nodeId != null) {
            sql = String.format(sql, nodeId);
        }
        final List<Column> columns = sqlInfo.getClusterInvalidationsColumns();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            int kind = Invalidations.MODIFIED;
            while (true) {
                final Set<RowId> rowIds = invalidations.getKindSet(kind);

                // reorganize by id
                final Map<Serializable, Set<String>> res = new HashMap<Serializable, Set<String>>();
                for (final RowId rowId : rowIds) {
                    Set<String> tableNames = res.get(rowId.id);
                    if (tableNames == null) {
                        res.put(rowId.id, tableNames = new HashSet<String>());
                    }
                    tableNames.add(rowId.tableName);
                }

                // do inserts
                for (final Entry<Serializable, Set<String>> en : res.entrySet()) {
                    final Serializable id = en.getKey();
                    final String fragments = join(en.getValue(), ' ');
                    if (logger.isLogEnabled()) {
                        logger.logSQL(sql, Arrays.<Serializable> asList(id, fragments, Long.valueOf(kind)));
                    }
                    Serializable frags;
                    if (sqlInfo.dialect.supportsArrays() && columns.get(1).getJdbcType() == Types.ARRAY) {
                        frags = fragments.split(" ");
                    } else {
                        frags = fragments;
                    }
                    columns.get(0).setToPreparedStatement(ps, 1, id);
                    columns.get(1).setToPreparedStatement(ps, 2, frags);
                    columns.get(2).setToPreparedStatement(ps, 3, Long.valueOf(kind));
                    ps.execute();
                }
                if (kind == Invalidations.MODIFIED) {
                    kind = Invalidations.DELETED;
                } else {
                    break;
                }
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not invalidate", e);
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    // join that works on a set
    protected static final String join(final Collection<String> strings, final char sep) {
        if (strings.isEmpty()) {
            throw new RuntimeException();
        }
        if (strings.size() == 1) {
            return strings.iterator().next();
        }
        int size = 0;
        for (final String word : strings) {
            size += word.length() + 1;
        }
        final StringBuilder buf = new StringBuilder(size);
        for (final String word : strings) {
            buf.append(word);
            buf.append(sep);
        }
        buf.setLength(size - 1);
        return buf.toString();
    }

    @Override
    public Invalidations getClusterInvalidations(final String nodeId) throws StorageException {
        final Invalidations invalidations = new Invalidations();
        String sql = sqlInfo.dialect.getClusterGetInvalidations();
        String sqldel = sqlInfo.dialect.getClusterDeleteInvalidations();
        if (nodeId != null) {
            sql = String.format(sql, nodeId);
            sqldel = String.format(sqldel, nodeId);
        }
        final List<Column> columns = sqlInfo.getClusterInvalidationsColumns();
        Statement st = null;
        try {
            st = connection.createStatement();
            if (logger.isLogEnabled()) {
                logger.log(sql);
            }
            final ResultSet rs = st.executeQuery(sql);
            int n = 0;
            while (rs.next()) {
                n++;
                final Serializable id = columns.get(0).getFromResultSet(rs, 1);
                final Serializable frags = columns.get(1).getFromResultSet(rs, 2);
                final int kind = ((Long) columns.get(2).getFromResultSet(rs, 3)).intValue();
                String[] fragments;
                if (sqlInfo.dialect.supportsArrays() && frags instanceof String[]) {
                    fragments = (String[]) frags;
                } else {
                    fragments = ((String) frags).split(" ");
                }
                invalidations.add(id, fragments, kind);
            }
            if (logger.isLogEnabled()) {
                // logCount(n);
                logger.log(" -> " + invalidations);
            }
            if (sqlInfo.dialect.isClusteringDeleteNeeded()) {
                if (logger.isLogEnabled()) {
                    logger.log(sqldel);
                }
                n = st.executeUpdate(sqldel);
                if (logger.isLogEnabled()) {
                    logger.logCount(n);
                }
            }
            return invalidations;
        } catch (final Exception e) {
            checkConnectionReset(e, true);
            throw new StorageException("Could not invalidate", e);
        } finally {
            if (st != null) {
                try {
                    closeStatement(st);
                } catch (final SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public Serializable getRootId(final Serializable repositoryId) throws StorageException {
        final String sql = sqlInfo.getSelectRootIdSql();
        try {
            if (logger.isLogEnabled()) {
                logger.logSQL(sql, Collections.singletonList(repositoryId));
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                ps.setObject(1, repositoryId);
                final ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    if (logger.isLogEnabled()) {
                        logger.log(" -> (none)");
                    }
                    return null;
                }
                final Column column = sqlInfo.getSelectRootIdWhatColumn();
                final Serializable id = column.getFromResultSet(rs, 1);
                if (logger.isLogEnabled()) {
                    logger.log(" -> " + model.MAIN_KEY + '=' + id);
                }
                // check that we didn't get several rows
                if (rs.next()) {
                    throw new StorageException("Row query for " + repositoryId + " returned several rows: " + sql);
                }
                return id;
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not select: " + sql, e);
        }
    }

    @Override
    public void setRootId(final Serializable repositoryId, final Serializable id) throws StorageException {
        final String sql = sqlInfo.getInsertRootIdSql();
        try {
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                final List<Column> columns = sqlInfo.getInsertRootIdColumns();
                List<Serializable> debugValues = null;
                if (logger.isLogEnabled()) {
                    debugValues = new ArrayList<Serializable>(2);
                }
                int i = 0;
                for (final Column column : columns) {
                    i++;
                    final String key = column.getKey();
                    Serializable v;
                    if (key.equals(model.MAIN_KEY)) {
                        v = id;
                    } else if (key.equals(model.REPOINFO_REPONAME_KEY)) {
                        v = repositoryId;
                    } else {
                        throw new RuntimeException(key);
                    }
                    column.setToPreparedStatement(ps, i, v);
                    if (debugValues != null) {
                        debugValues.add(v);
                    }
                }
                if (debugValues != null) {
                    logger.logSQL(sql, debugValues);
                    debugValues.clear();
                }
                ps.execute();
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not insert: " + sql, e);
        }
    }

    // uses JDBCRowMapper
    @Override
    public Serializable getVersionIdByLabel(final Serializable versionSeriesId, final String label) throws StorageException {
        final SQLInfoSelect select = sqlInfo.selectVersionBySeriesAndLabel;
        final Map<String, Serializable> criteriaMap = new HashMap<String, Serializable>();
        criteriaMap.put(model.MAIN_IS_VERSION_KEY, Boolean.TRUE);
        criteriaMap.put(model.VERSION_VERSIONABLE_KEY, versionSeriesId);
        criteriaMap.put(model.VERSION_LABEL_KEY, label);
        final List<Row> rows = getSelectRows(model.VERSION_TABLE_NAME, select, criteriaMap, null, true);
        return rows == null ? null : rows.get(0).id;
    }

    // uses JDBCRowMapper
    @Override
    public Serializable getLastVersionId(final Serializable versionSeriesId) throws StorageException {
        final SQLInfoSelect select = sqlInfo.selectVersionsBySeriesDesc;
        final Map<String, Serializable> criteriaMap = new HashMap<String, Serializable>();
        criteriaMap.put(model.MAIN_IS_VERSION_KEY, Boolean.TRUE);
        criteriaMap.put(model.VERSION_VERSIONABLE_KEY, versionSeriesId);
        final List<Row> maps = getSelectRows(model.VERSION_TABLE_NAME, select, criteriaMap, null, true);
        return maps == null ? null : maps.get(0).id;
    }

    protected QueryMaker findQueryMaker(final String query) throws StorageException {
        for (final Class<? extends QueryMaker> klass : queryMakerService.getQueryMakers()) {
            QueryMaker queryMaker;
            try {
                queryMaker = klass.newInstance();
            } catch (final Exception e) {
                throw new StorageException(e);
            }
            if (queryMaker.accepts(query)) {
                return queryMaker;
            }
        }
        return null;
    }

    @Override
    public PartialList<Serializable> query(final String query, final QueryFilter queryFilter, final boolean countTotal) throws StorageException {
        final QueryMaker queryMaker = findQueryMaker(query);
        if (queryMaker == null) {
            throw new StorageException("No QueryMaker accepts query: " + query);
        }
        final QueryMaker.Query q = queryMaker.buildQuery(sqlInfo, model, pathResolver, query, queryFilter);

        if (q == null) {
            logger.log("Query cannot return anything due to conflicting clauses");
            return new PartialList<Serializable>(Collections.<Serializable> emptyList(), 0);
        }

        long limit = queryFilter.getLimit();
        long offset = queryFilter.getOffset();

        if (logger.isLogEnabled()) {
            String sql = q.selectInfo.sql;
            if (limit != 0) {
                sql += " -- LIMIT " + limit + " OFFSET " + offset;
            }
            if (countTotal) {
                sql += " -- COUNT TOTAL";
            }
            logger.logSQL(sql, q.selectParams);
        }

        String sql = q.selectInfo.sql;

        if (!countTotal && limit > 0 && sqlInfo.dialect.supportsPaging()) {
            // full result set not needed for counting
            sql += " " + sqlInfo.dialect.getPagingClause(limit, offset);
            limit = 0;
            offset = 0;
        }

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int i = 1;
            for (final Object object : q.selectParams) {
                if (object instanceof Calendar) {
                    final Calendar cal = (Calendar) object;
                    final Timestamp ts = new Timestamp(cal.getTimeInMillis());
                    ps.setTimestamp(i++, ts, cal); // cal passed for timezone
                } else if (object instanceof String[]) {
                    final Array array = sqlInfo.dialect.createArrayOf(Types.VARCHAR, (Object[]) object, connection);
                    ps.setArray(i++, array);
                } else {
                    ps.setObject(i++, object);
                }
            }
            final ResultSet rs = ps.executeQuery();

            // limit/offset
            long totalSize = -1;
            boolean available;
            if (limit == 0 || offset == 0) {
                available = rs.first();
                if (!available) {
                    totalSize = 0;
                }
                if (limit == 0) {
                    limit = -1; // infinite
                }
            } else {
                available = rs.absolute((int) offset + 1);
            }

            final Column column = q.selectInfo.whatColumns.get(0);
            final List<Serializable> ids = new LinkedList<Serializable>();
            int rowNum = 0;
            while (available && limit != 0) {
                final Serializable id = column.getFromResultSet(rs, 1);
                ids.add(id);
                rowNum = rs.getRow();
                available = rs.next();
                limit--;
            }

            // total size
            if (countTotal && totalSize == -1) {
                if (!available && rowNum != 0) {
                    // last row read was the actual last
                    totalSize = rowNum;
                } else {
                    // available if limit reached with some left
                    // rowNum == 0 if skipped too far
                    rs.last();
                    totalSize = rs.getRow();
                }
            }

            if (logger.isLogEnabled()) {
                logger.logIds(ids, countTotal, totalSize);
            }

            return new PartialList<Serializable>(ids, totalSize);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Invalid query: " + query, e);
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    log.error("Cannot close connection", e);
                }
            }
        }
    }

    // queryFilter used for principals and permissions
    @Override
    public IterableQueryResult queryAndFetch(final String query, final String queryType, final QueryFilter queryFilter, final Object... params)
            throws StorageException {
        final QueryMaker queryMaker = findQueryMaker(queryType);
        if (queryMaker == null) {
            throw new StorageException("No QueryMaker accepts query: " + queryType + ": " + query);
        }
        try {
            return new ResultSetQueryResult(queryMaker, query, queryFilter, pathResolver, this, params);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Invalid query: " + queryType + ": " + query, e);
        }
    }

    @Override
    public void updateReadAcls() throws StorageException {
        if (!sqlInfo.dialect.supportsReadAcl()) {
            return;
        }
        log.debug("updateReadAcls: updating ...");
        Statement st = null;
        try {
            st = connection.createStatement();
            final String sql = sqlInfo.dialect.getUpdateReadAclsSql();
            logger.log(sql);
            st.execute(sql);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Failed to update read acls", e);
        } finally {
            if (st != null) {
                try {
                    closeStatement(st);
                } catch (final SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.debug("updateReadAcls: done.");
    }

    @Override
    public void rebuildReadAcls() throws StorageException {
        if (!sqlInfo.dialect.supportsReadAcl()) {
            return;
        }
        log.debug("rebuildReadAcls: rebuilding ...");
        Statement st = null;
        try {
            st = connection.createStatement();
            final String sql = sqlInfo.dialect.getRebuildReadAclsSql();
            logger.log(sql);
            st.execute(sql);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Failed to rebuild read acls", e);
        } finally {
            if (st != null) {
                try {
                    closeStatement(st);
                } catch (final SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.debug("rebuildReadAcls: done.");
    }

    /*
    * ----- Locking -----
    */

    @Override
    public Lock getLock(final Serializable id) throws StorageException {
        checkConnectionValid();
        final RowId rowId = new RowId(Model.LOCK_TABLE_NAME, id);
        Row row;
        try {
            row = readSimpleRow(rowId);
        } catch (final ConnectionResetException e) {
            // retry once
            row = readSimpleRow(rowId);
        }
        return row == null ? null : new Lock((String) row.get(Model.LOCK_OWNER_KEY), (Calendar) row.get(Model.LOCK_CREATED_KEY));
    }

    @Override
    public Lock setLock(final Serializable id, final Lock lock) throws StorageException {
        final Lock oldLock = getLock(id);
        if (oldLock == null) {
            final Row row = new Row(Model.LOCK_TABLE_NAME, id);
            row.put(Model.LOCK_OWNER_KEY, lock.getOwner());
            row.put(Model.LOCK_CREATED_KEY, lock.getCreated());
            insertSimpleRow(row);
        }
        return oldLock;
    }

    @Override
    public Lock removeLock(final Serializable id, final String owner, final boolean force) throws StorageException {
        final Lock oldLock = force ? null : getLock(id);
        if (!force && owner != null) {
            if (oldLock == null) {
                // not locked, nothing to do
                return null;
            }
            if (!LockManager.canLockBeRemoved(oldLock, owner)) {
                // existing mismatched lock, flag failure
                return new Lock(oldLock, true);
            }
        }
        if (force || oldLock != null) {
            deleteRows(Model.LOCK_TABLE_NAME, id);
        }
        return oldLock;
    }

    /*
    * ----- XAResource -----
    */

    @Override
    public void start(final Xid xid, final int flags) throws XAException {
        try {
            checkConnectionValid();
            xaresource.start(xid, flags);
            if (log.isDebugEnabled()) {
                log.debug("XA start on " + xid.getFormatId());
            }
        } catch (final StorageException e) {
            throw (XAException) new XAException(XAException.XAER_RMERR).initCause(e);
        } catch (final XAException e) {
            checkConnectionReset(e);
            log.error("XA start error on " + xid.getFormatId(), e);
            throw e;
        }
    }

    @Override
    public void end(final Xid xid, final int flags) throws XAException {
        try {
            xaresource.end(xid, flags);
            if (log.isDebugEnabled()) {
                log.debug("XA end on " + xid.getFormatId());
            }
        } catch (final NullPointerException e) {
            // H2 when no active transaction
            log.error("XA end error on " + xid, e);
            throw (XAException) new XAException(XAException.XAER_RMERR).initCause(e);
        } catch (final XAException e) {
            log.error("XA end error on " + xid, e);
            throw e;
        }
    }

    @Override
    public int prepare(final Xid xid) throws XAException {
        try {
            return xaresource.prepare(xid);
        } catch (final XAException e) {
            log.error("XA prepare error on " + xid, e);
            throw e;
        }
    }

    @Override
    public void commit(final Xid xid, final boolean onePhase) throws XAException {
        try {
            xaresource.commit(xid, onePhase);
        } catch (final XAException e) {
            log.error("XA commit error on " + xid, e);
            throw e;
        }
    }

    // rollback interacts with caches so is in RowMapper

    @Override
    public void forget(final Xid xid) throws XAException {
        xaresource.forget(xid);
    }

    @Override
    public Xid[] recover(final int flag) throws XAException {
        return xaresource.recover(flag);
    }

    @Override
    public boolean setTransactionTimeout(final int seconds) throws XAException {
        return xaresource.setTransactionTimeout(seconds);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return xaresource.getTransactionTimeout();
    }

    @Override
    public boolean isSameRM(final XAResource xares) throws XAException {
        throw new UnsupportedOperationException();
    }

}