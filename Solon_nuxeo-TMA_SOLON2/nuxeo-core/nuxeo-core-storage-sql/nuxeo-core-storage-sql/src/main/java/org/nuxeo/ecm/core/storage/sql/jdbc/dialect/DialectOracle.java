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
* Benoit Delbosc
*/

package org.nuxeo.ecm.core.storage.sql.jdbc.dialect;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.BinaryManager;
import org.nuxeo.ecm.core.storage.sql.ColumnType;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.ModelFulltext;
import org.nuxeo.ecm.core.storage.sql.RepositoryDescriptor;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.runtime.api.ConnectionHelper;

/**
* Oracle-specific dialect.
*
* @author Florent Guillaume
*/
public class DialectOracle extends Dialect {

    private static final Log log = LogFactory.getLog(DialectOracle.class);

    protected final String fulltextParameters;

    protected boolean pathOptimizationsEnabled;
    

    public DialectOracle(final DatabaseMetaData metadata, 
    		final BinaryManager binaryManager, 
    		final RepositoryDescriptor repositoryDescriptor) throws StorageException {
        super(metadata, binaryManager, repositoryDescriptor);
        fulltextParameters = repositoryDescriptor == null ? null 
        		: repositoryDescriptor.fulltextAnalyzer == null ? ""
        				: repositoryDescriptor.fulltextAnalyzer;
        pathOptimizationsEnabled = repositoryDescriptor == null ? false 
        		: repositoryDescriptor.pathOptimizationsEnabled;
    }

    @Override
    public String getConnectionSchema(final Connection connection) 
    		throws SQLException {
        final Statement st = connection.createStatement();
        final String sql = "SELECT SYS_CONTEXT('USERENV', 'SESSION_USER') FROM DUAL";
        log.trace("SQL: " + sql);
        final ResultSet rs = st.executeQuery(sql);
        rs.next();
        final String user = rs.getString(1);
        log.trace("SQL: -> " + user);
        st.close();
        return user;
    }

    @Override
    public String getCascadeDropConstraintsString() {
        return " CASCADE CONSTRAINTS";
    }

    @Override
    public String getAddColumnString() {
        return "ADD";
    }

    @Override
    public JDBCInfo getJDBCTypeAndString(final ColumnType type) {
        switch (type.spec) {
        case STRING:
            if (type.isUnconstrained()) {
                return jdbcInfo("NVARCHAR2(2000)", Types.VARCHAR);
            } else if (type.isClob() || type.length > 2000) {
                return jdbcInfo("NCLOB", Types.CLOB);
            } else {
                return jdbcInfo("NVARCHAR2(%d)", type.length, Types.VARCHAR);
            }
        case BOOLEAN:
            return jdbcInfo("NUMBER(1,0)", Types.BIT);
        case LONG:
            return jdbcInfo("NUMBER(19,0)", Types.BIGINT);
        case DOUBLE:
            return jdbcInfo("DOUBLE PRECISION", Types.DOUBLE);
        case TIMESTAMP:
            return jdbcInfo("TIMESTAMP", Types.TIMESTAMP);
        case BLOBID:
            return jdbcInfo("VARCHAR2(40)", Types.VARCHAR);
            // -----
        case NODEID:
        case NODEIDFK:
        case NODEIDFKNP:
        case NODEIDFKMUL:
        case NODEIDFKNULL:
        case NODEIDPK:
        case NODEVAL:
            return jdbcInfo("VARCHAR2(36)", Types.VARCHAR);
        case SYSNAME:
        case SYSNAMEARRAY:
            return jdbcInfo("VARCHAR2(250)", Types.VARCHAR);
        case TINYINT:
            return jdbcInfo("NUMBER(3,0)", Types.TINYINT);
        case INTEGER:
            return jdbcInfo("NUMBER(10,0)", Types.INTEGER);
        case FTINDEXED:
            return jdbcInfo("CLOB", Types.CLOB);
        case FTSTORED:
            return jdbcInfo("NCLOB", Types.CLOB);
        case CLUSTERNODE:
            return jdbcInfo("VARCHAR(25)", Types.VARCHAR);
        case CLUSTERFRAGS:
            return jdbcInfo("VARCHAR2(4000)", Types.VARCHAR);
        }
        throw new AssertionError(type);
    }

    @Override
    public boolean isAllowedConversion(final int expected, final int actual, 
    		final String actualName, final int actualSize) {
        // Oracle internal conversions
        if (expected == Types.DOUBLE && actual == Types.FLOAT) {
            return true;
        }
        if (expected == Types.VARCHAR && actual == Types.OTHER 
        		&& actualName.equals("NVARCHAR2")) {
            return true;
        }
        if (expected == Types.CLOB && actual == Types.OTHER 
        		&& actualName.equals("NCLOB")) {
            return true;
        }
        if (expected == Types.BIT && actual == Types.DECIMAL 
        		&& actualName.equals("NUMBER") && actualSize == 1) {
            return true;
        }
        if (expected == Types.TINYINT && actual == Types.DECIMAL 
        		&& actualName.equals("NUMBER") && actualSize == 3) {
            return true;
        }
        if (expected == Types.INTEGER && actual == Types.DECIMAL 
        		&& actualName.equals("NUMBER") && actualSize == 10) {
            return true;
        }
        if (expected == Types.BIGINT && actual == Types.DECIMAL 
        		&& actualName.equals("NUMBER") && actualSize == 19) {
            return true;
        }
        // CLOB vs VARCHAR compatibility
        if (expected == Types.VARCHAR && actual == Types.OTHER 
        		&& actualName.equals("NCLOB")) {
            return true;
        }
        if (expected == Types.CLOB && actual == Types.OTHER 
        		&& actualName.equals("NVARCHAR2")) {
            return true;
        }
        return false;
    }

    @Override
    public void setToPreparedStatement(final PreparedStatement ps, final int index, 
    		final Serializable value, final Column column) throws SQLException {
        switch (column.getJdbcType()) {
        case Types.VARCHAR:
        case Types.CLOB:
            setToPreparedStatementString(ps, index, value, column);
            return;
        case Types.BIT:
            ps.setBoolean(index, ((Boolean) value).booleanValue());
            return;
        case Types.TINYINT:
        case Types.SMALLINT:
            ps.setInt(index, ((Long) value).intValue());
            return;
        case Types.INTEGER:
        case Types.BIGINT:
            ps.setLong(index, ((Long) value).longValue());
            return;
        case Types.DOUBLE:
            ps.setDouble(index, ((Double) value).doubleValue());
            return;
        case Types.TIMESTAMP:
            setToPreparedStatementTimestamp(ps, index, value, column);
            return;
        default:
            throw new SQLException("Unhandled JDBC type: " 
            		+ column.getJdbcType());
        }
    }

    @Override
    @SuppressWarnings("boxing")
    public Serializable getFromResultSet(final ResultSet rs, final int index, final Column column) 
    		throws SQLException {
        switch (column.getJdbcType()) {
        case Types.VARCHAR:
            return getFromResultSetString(rs, index, column);
        case Types.CLOB:
            // Oracle cannot read CLOBs using rs.getString when the ResultSet is
            // a ScrollableResultSet (the standard OracleResultSetImpl works
            // fine).
            final Reader r = rs.getCharacterStream(index);
            if (r == null) {
                return null;
            }
            final StringBuilder sb = new StringBuilder();
            try {
                int n;
                final char[] buffer = new char[4096];
                while ((n = r.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, n));
                }
            } catch (final IOException e) {
                log.error("Cannot read CLOB", e);
            }
            return sb.toString();
        case Types.BIT:
            return rs.getBoolean(index);
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.BIGINT:
            return rs.getLong(index);
        case Types.DOUBLE:
            return rs.getDouble(index);
        case Types.TIMESTAMP:
            return getFromResultSetTimestamp(rs, index, column);
        }
        throw new SQLException("Unhandled JDBC type: " + column.getJdbcType());
    }

    @Override
    protected int getMaxNameSize() {
        return 30;
    }

    @Override
    /* Avoid DRG-11439: index name length exceeds maximum of 25 bytes */
    protected int getMaxIndexNameSize() {
        return 25;
    }

    @Override
    public String getCreateFulltextIndexSql(final String indexName, 
    		final String quotedIndexName, final Table table, final List<Column> columns,
            final Model model) {
        return String.format(
        		"CREATE INDEX %s ON %s(%s) INDEXTYPE IS CTXSYS.CONTEXT " 
        				+ "PARAMETERS('%s SYNC (ON COMMIT) TRANSACTIONAL')",
                quotedIndexName, table.getQuotedName(), 
                columns.get(0).getQuotedName(), fulltextParameters);
    }
    
    protected static final String CHARS_RESERVED_STR = "%${";
    
    protected static final Set<Character> CHARS_RESERVED = new HashSet<Character>(
            Arrays.asList(ArrayUtils.toObject(CHARS_RESERVED_STR.toCharArray())));

    @Override
    public String getDialectFulltextQuery(String query) {
        query = query.replace("*", "%"); // reserved, words with it not quoted
        final FulltextQuery ft = analyzeFulltextQuery(query);
        if (ft == null) {
            return "DONTMATCHANYTHINGFOREMPTYQUERY";
        }
        return translateFulltext(ft, "OR", "AND", "NOT", "{", "}",
        		CHARS_RESERVED, "", "", true);
    }

    // SELECT ..., (SCORE(1) / 100) AS "_nxscore"
    // FROM ... LEFT JOIN fulltext ON fulltext.id = hierarchy.id
    // WHERE ... AND CONTAINS(fulltext.fulltext, ?, 1) > 0
    // ORDER BY "_nxscore" DESC
    @Override
    public FulltextMatchInfo getFulltextScoredMatchInfo(final String fulltextQuery, 
    		final String indexName, final int nthMatch, final Column mainColumn, final Model model, 
    		final Database database) {
        final String indexSuffix = model.getFulltextIndexSuffix(indexName);
        final Table ft = database.getTable(model.FULLTEXT_TABLE_NAME);
        final Column ftMain = ft.getColumn(model.MAIN_KEY);
        final Column ftColumn = ft.getColumn(model.FULLTEXT_FULLTEXT_KEY + indexSuffix);
        final String score = String.format("SCORE(%d)", nthMatch);
        final String nthSuffix = nthMatch == 1 ? "" : String.valueOf(nthMatch);
        final FulltextMatchInfo info = new FulltextMatchInfo();
        if (nthMatch == 1) {
            // Need only one JOIN involving the fulltext table
            info.joins = Collections.singletonList(new Join(Join.INNER, 
            		ft.getQuotedName(), null, null, ftMain.getFullQuotedName(),
            		mainColumn.getFullQuotedName()));
        }
        info.whereExpr = String.format("CONTAINS(%s, ?, %d) > 0", 
        		ftColumn.getFullQuotedName(), nthMatch);
        info.whereExprParam = fulltextQuery;
        info.scoreExpr = String.format("(%s / 100)", score);
        info.scoreAlias = openQuote() + "_nxscore" + nthSuffix + closeQuote();
        info.scoreCol = new Column(mainColumn.getTable(), null, 
        		ColumnType.DOUBLE, null);
        return info;
    }

    @Override
    public boolean getMaterializeFulltextSyntheticColumn() {
        return true;
    }

    @Override
    public int getFulltextIndexedColumns() {
        return 1;
    }

    @Override
    public boolean supportsUpdateFrom() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesUpdateFromRepeatSelf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean needsOriginalColumnInGroupBy() {
        // http://download.oracle.com/docs/cd/B19306_01/server.102/b14200/statements_10002.htm#i2080424
        // The alias can be used in the order_by_clause but not other clauses in
        // the query.
        return true;
    }

    @Override
    public boolean needsOracleJoins() {
        return true;
    }

    @Override
    public String getClobCast(final boolean inOrderBy) {
        return "CAST(%s AS NVARCHAR2(%d))";
    }

    @Override
    public boolean supportsReadAcl() {
        return aclOptimizationsEnabled;
    }

    @Override
    public String getReadAclsCheckSql(final String idColumnName) {
        return String.format(
        		"%s IN (SELECT COLUMN_VALUE FROM TABLE(nx_get_read_acls_for(?)))", 
        		idColumnName);
    }

    @Override
    public String getUpdateReadAclsSql() {
        return "{CALL nx_update_read_acls}";
    }

    @Override
    public String getRebuildReadAclsSql() {
        return "{CALL nx_rebuild_read_acls}";
    }

    @Override
    public String getSecurityCheckSql(final String idColumnName) {
        return String.format("NX_ACCESS_ALLOWED(%s, ?, ?) = 1", idColumnName);
    }

    @Override
    public String getInTreeSql(final String idColumnName) {
        if (pathOptimizationsEnabled) {
            return String.format(
            		"EXISTS(SELECT 1 FROM ancestors WHERE hierarchy_id = %s AND ? MEMBER OF ancestors)", 
            		idColumnName);
        } else {
            return String.format("NX_IN_TREE(%s, ?) = 1", idColumnName);
        }
    }

    @Override
    public boolean isClusteringSupported() {
        return true;
    }

    @Override
    public String getClusterNodeIdSql() {
        return "SELECT SYS_CONTEXT('USERENV', 'SID') || ',' || SERIAL# "
                + "FROM GV$SESSION WHERE SID = SYS_CONTEXT('USERENV', 'SID') "
                + "AND INST_ID = SYS_CONTEXT('USERENV', 'INSTANCE')";
    }
    
    /*
    * For Oracle we don't use a function to return values and delete them at
    * the same time, because pipelined functions that need to do DML have to do
    * it in an autonomous transaction which could cause consistency issues.
    */
    @Override
    public boolean isClusteringDeleteNeeded() {
        return true;
    }

    @Override
    public String getClusterInsertInvalidations() {
        return "{CALL NX_CLUSTER_INVAL(?, ?, ?, '%s')}";
    }

    @Override
    public String getClusterGetInvalidations() {
        return "SELECT id, fragments, kind FROM cluster_invals " 
        		+ "WHERE nodeid = '%s'";
    }

    @Override
    public String getClusterDeleteInvalidations() {
        return "DELETE FROM cluster_invals WHERE nodeid = '%s'";
    }

    @Override
    public boolean supportsWith() {
        return false;
        // return !aclOptimizationsEnabled;
    }

    @Override
    public boolean supportsArrays() {
        return true;
    }

    @Override
    public boolean supportsArraysReturnInsteadOfRows() {
        return true;
    }
    
    @Override
    public boolean hasNullEmptyString() {
        return true;
    }

    private static boolean initialized;

    private static Constructor<?> arrayDescriptorConstructor;

    private static Constructor<?> arrayConstructor;

    private static void init() throws SQLException {
        if (!initialized) {
            try {
                final Class<?> arrayDescriptorClass = Class.forName("oracle.sql.ArrayDescriptor");
                arrayDescriptorConstructor = arrayDescriptorClass.getConstructor(
                		String.class, Connection.class);
                final Class<?> arrayClass = Class.forName("oracle.sql.ARRAY");
                arrayConstructor = arrayClass.getConstructor(
                		arrayDescriptorClass, Connection.class, Object.class);
            } catch (final Exception e) {
                throw new SQLException(e.toString());
            }
            initialized = true;
        }
    }

    // use reflection to avoid linking dependencies
    @Override
    public Array createArrayOf(final int type, final Object[] elements, 
    		Connection connection) throws SQLException {
        if (elements == null || elements.length == 0) {
            return null;
        }
        init();
        try {
            connection = ConnectionHelper.unwrap(connection);
            final Object arrayDescriptor = arrayDescriptorConstructor.newInstance(
            		"NX_STRING_TABLE", connection);
            return (Array) arrayConstructor.newInstance(arrayDescriptor, 
            		connection, elements);
        } catch (final Exception e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    @Override
    public String getSQLStatementsFilename() {
        return "nuxeovcs/oracle.sql.txt";
    }

    @Override
    public String getTestSQLStatementsFilename() {
        return "nuxeovcs/oracle.test.sql.txt";
    }

    @Override
    public Map<String, Serializable> getSQLStatementsProperties(final Model model, 
    		final Database database) {
        final Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("idType", "VARCHAR2(36)");
        properties.put("argIdType", "VARCHAR2"); // in function args
        properties.put("aclOptimizationsEnabled", 
        		Boolean.valueOf(aclOptimizationsEnabled));
        properties.put("pathOptimizationsEnabled", 
        		Boolean.valueOf(pathOptimizationsEnabled));
        properties.put("fulltextEnabled", Boolean.valueOf(!fulltextDisabled));
        //properties.put("softDeleteEnabled", Boolean.valueOf(softDeleteEnabled));
        if (!fulltextDisabled) {
            final Table ft = database.getTable(model.FULLTEXT_TABLE_NAME);
            properties.put("fulltextTable", ft.getQuotedName());
            final ModelFulltext fti = model.getFulltextInfo();
            final List<String> lines = new ArrayList<String>(fti.indexNames.size());
            for (final String indexName : fti.indexNames) {
                final String suffix = model.getFulltextIndexSuffix(indexName);
                final Column ftft = ft.getColumn(model.FULLTEXT_FULLTEXT_KEY + suffix);
                final Column ftst = ft.getColumn(model.FULLTEXT_SIMPLETEXT_KEY 
                		+ suffix);
                final Column ftbt = ft.getColumn(model.FULLTEXT_BINARYTEXT_KEY 
                		+ suffix);
                final String line = String.format(
                		" :NEW.%s := :NEW.%s || :NEW.%s; ", 
                		ftft.getQuotedName(), ftst.getQuotedName(),
                        ftbt.getQuotedName());
                lines.add(line);
            }
            properties.put("fulltextTriggerStatements", 
            		StringUtils.join(lines, "\n"));
        }
        final String[] permissions = NXCore.getSecurityService().getPermissionsToCheck(
        		SecurityConstants.BROWSE);
        final List<String> permsList = new LinkedList<String>();
        for (final String perm : permissions) {
            permsList.add(String.format(
            		" INTO ACLR_PERMISSION VALUES ('%s')", perm));
        }
        properties.put("readPermissions", StringUtils.join(permsList, "\n"));
        return properties;
    }

    @Override
    public boolean isConnectionClosedException(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof SocketException) {
            return true;
        }
        // XAResource.start:
        // oracle.jdbc.xa.OracleXAException
        Integer err = Integer.valueOf(0);
        try {
            final Method m = t.getClass().getMethod("getOracleError");
            err = (Integer) m.invoke(t);
        } catch (final Exception e) {
            // ignore
        }
        if (Integer.valueOf(0).equals(err)) {
            try {
                err = ((SQLException) t).getErrorCode();
            } catch (final Exception e) {
                // ignore
            }
        }
        switch (err.intValue()) {
        case 17002: // ORA-17002 IO Exception
        case 17008: // ORA-17008 Closed Connection
        case 17410: // ORA-17410 No more data to read from socket
            return true;
        }
        return false;
    }

    @Override
    public String getValidationQuery() {
        return "SELECT 1 FROM DUAL";
    }

    @Override
    public String getBlobLengthFunction() {
        return "LENGTHB";
    }
    
//    @Override
//    public String getAncestorsIdsSql() {
//        return "SELECT NX_ANCESTORS(?) FROM DUAL";
//    } 
//
//    @Override
//    public String getSoftDeleteSql() {
//        return "{CALL NX_DELETE(?, ?)}";
//    } 
//
//    @Override
//    public String getSoftDeleteCleanupSql() {
//        return "{CALL NX_DELETE_PURGE(?, ?, ?)}";
//    }

    @Override
    public String getDateCast() {
        // CAST(%s AS DATE) doesn't work, it doesn't compare exactly to DATE
        // literals because the internal representation seems to be a float and
        // CAST AS DATE does not truncate it
        return "TRUNC(%s)";
    }

}