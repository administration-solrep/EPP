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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.Invalidations;
import org.nuxeo.ecm.core.storage.sql.Invalidations.InvalidationsPair;
import org.nuxeo.ecm.core.storage.sql.InvalidationsQueue;
import org.nuxeo.ecm.core.storage.sql.Mapper;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.PropertyType;
import org.nuxeo.ecm.core.storage.sql.Row;
import org.nuxeo.ecm.core.storage.sql.RowId;
import org.nuxeo.ecm.core.storage.sql.RowMapper;
import org.nuxeo.ecm.core.storage.sql.SimpleFragment;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Update;

/**
* A {@link JDBCRowMapper} maps {@link Row}s to and from a JDBC database.
*/
public class JDBCRowMapper extends JDBCConnection implements RowMapper {

    /**
    * Cluster node handler, or {@code null} if this {@link Mapper} is not the
    * cluster node mapper.
    */
    private final ClusterNodeHandler clusterNodeHandler;

    /**
    * Queue of invalidations received for this cluster node.
    */
    private final InvalidationsQueue queue;

    public JDBCRowMapper(final Model model, final SQLInfo sqlInfo, final XADataSource xadatasource, final ClusterNodeHandler clusterNodeHandler,
            final JDBCConnectionPropagator connectionPropagator) throws StorageException {
        super(model, sqlInfo, xadatasource, connectionPropagator);
        this.clusterNodeHandler = clusterNodeHandler;
        queue = new InvalidationsQueue("cluster");
        if (clusterNodeHandler != null) {
            clusterNodeHandler.addQueue(queue);
        }
    }

    @Override
    public void close() {
        super.close();
        if (clusterNodeHandler != null) {
            clusterNodeHandler.removeQueue(queue);
        }
    }

    @Override
    public InvalidationsPair receiveInvalidations() throws StorageException {
        Invalidations invalidations = null;
        if (clusterNodeHandler != null && connection != null) {
            receiveClusterInvalidations();
            invalidations = queue.getInvalidations();
        }
        return invalidations == null ? null : new InvalidationsPair(invalidations, null);
    }

    protected void receiveClusterInvalidations() throws StorageException {
        final Invalidations invalidations = clusterNodeHandler.receiveClusterInvalidations();
        // send received invalidations to all mappers
        if (invalidations != null && !invalidations.isEmpty()) {
            clusterNodeHandler.propagateInvalidations(invalidations, null);
        }
    }

    @Override
    public void sendInvalidations(final Invalidations invalidations) throws StorageException {
        if (clusterNodeHandler != null) {
            clusterNodeHandler.sendClusterInvalidations(invalidations);
        }
    }

    @Override
    public void clearCache() {
        // no cache
    }

    @Override
    public void rollback(final Xid xid) throws XAException {
        try {
            xaresource.rollback(xid);
        } catch (final XAException e) {
            logger.error("XA error on rollback: " + e);
            throw e;
        }
    }

    protected CollectionIO getCollectionIO(final String tableName) {
        return tableName.equals(model.ACL_TABLE_NAME) ? ACLCollectionIO.INSTANCE : ScalarCollectionIO.INSTANCE;
    }

    /*
    * ----- RowIO -----
    */

    @Override
    public List<? extends RowId> read(final Collection<RowId> rowIds) throws StorageException {
        // reorganize by table
        final Map<String, Set<Serializable>> tableIds = new HashMap<String, Set<Serializable>>();
        for (final RowId rowId : rowIds) {
            Set<Serializable> ids = tableIds.get(rowId.tableName);
            if (ids == null) {
                tableIds.put(rowId.tableName, ids = new HashSet<Serializable>());
            }
            ids.add(rowId.id);
        }
        // read on each table
        final List<RowId> res = new ArrayList<RowId>(rowIds.size());
        for (final Entry<String, Set<Serializable>> en : tableIds.entrySet()) {
            final String tableName = en.getKey();
            final Set<Serializable> ids = new HashSet<Serializable>(en.getValue());
            final int size = ids.size();
            final int chunkSize = sqlInfo.getMaximumArgsForIn();
            List<Row> rows;
            if (size > chunkSize) {
                final List<Serializable> idList = new ArrayList<Serializable>(ids);
                rows = new ArrayList<Row>(size);
                for (int start = 0; start < size; start += chunkSize) {
                    int end = start + chunkSize;
                    if (end > size) {
                        end = size;
                    }
                    // needs to be Serializable -> copy
                    final List<Serializable> chunkIds = new ArrayList<Serializable>(idList.subList(start, end));
                    List<Row> chunkRows;
                    if (model.isCollectionFragment(tableName)) {
                        chunkRows = readCollectionArrays(tableName, chunkIds);
                    } else {
                        chunkRows = readSimpleRows(tableName, chunkIds);
                    }
                    rows.addAll(chunkRows);
                }
            } else {
                if (model.isCollectionFragment(tableName)) {
                    rows = readCollectionArrays(tableName, ids);
                } else {
                    rows = readSimpleRows(tableName, ids);
                }
            }
            // check we have all the ids (readSimpleRows may have some
            // missing)
            for (final Row row : rows) {
                res.add(row);
                ids.remove(row.id);
            }
            // for the missing ids record an empty RowId
            for (final Serializable id : ids) {
                res.add(new RowId(tableName, id));
            }
        }
        return res;
    }

    /**
    * Gets a list of rows for {@link SimpleFragment}s from the database, given
    * the table name and the ids.
    *
    * @param tableName the table name
    * @param ids the ids
    * @return the list of rows, without the missing ones
    */
    protected List<Row> readSimpleRows(final String tableName, final Collection<Serializable> ids) throws StorageException {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        final SQLInfoSelect select = sqlInfo.getSelectFragmentsByIds(tableName, ids.size());
        final Map<String, Serializable> criteriaMap = Collections.singletonMap(model.MAIN_KEY, (Serializable) ids);
        return getSelectRows(tableName, select, criteriaMap, null, false);
    }

    /**
    * Reads several collection rows, given a table name and the ids.
    *
    * @param tableName the table name
    * @param ids the ids
    */
    protected List<Row> readCollectionArrays(final String tableName, final Collection<Serializable> ids) throws StorageException {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        final String[] orderBys = { model.MAIN_KEY, model.COLL_TABLE_POS_KEY }; // clusters
        // results
        final Set<String> skipColumns = new HashSet<String>(Arrays.asList(model.COLL_TABLE_POS_KEY));
        final SQLInfoSelect select = sqlInfo.getSelectFragmentsByIds(tableName, ids.size(), orderBys, skipColumns);

        final String sql = select.sql;
        try {
            if (logger.isLogEnabled()) {
                logger.logSQL(sql, ids);
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                int i = 1;
                for (final Serializable id : ids) {
                    ps.setObject(i++, id);
                }
                final ResultSet rs = ps.executeQuery();

                // get all values from result set, separate by ids
                // the result set is ordered by id, pos
                final CollectionIO io = getCollectionIO(tableName);
                final PropertyType ftype = model.getCollectionFragmentType(tableName);
                final PropertyType type = ftype.getArrayBaseType();
                Serializable curId = null;
                List<Serializable> list = null;
                final Serializable[] returnId = new Serializable[1];
                final int[] returnPos = { -1 };
                final List<Row> res = new LinkedList<Row>();
                final Set<Serializable> remainingIds = new HashSet<Serializable>(ids);
                while (rs.next()) {
                    final Serializable value = io.getCurrentFromResultSet(rs, select.whatColumns, model, returnId, returnPos);
                    final Serializable newId = returnId[0];
                    if (newId != null && !newId.equals(curId)) {
                        // flush old list
                        if (list != null) {
                            res.add(new Row(tableName, curId, type.collectionToArray(list)));
                            remainingIds.remove(curId);
                        }
                        curId = newId;
                        list = new ArrayList<Serializable>();
                    }
                    list.add(value);
                }
                if (curId != null && list != null) {
                    // flush last list
                    res.add(new Row(tableName, curId, type.collectionToArray(list)));
                    remainingIds.remove(curId);
                }

                // fill empty ones
                if (!remainingIds.isEmpty()) {
                    final Serializable[] emptyArray = ftype.getEmptyArray();
                    for (final Serializable id : remainingIds) {
                        res.add(new Row(tableName, id, emptyArray));
                    }
                }
                if (logger.isLogEnabled()) {
                    for (final Row row : res) {
                        logger.log(" -> " + row);
                    }
                }
                return res;
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not select: " + sql, e);
        }
    }

    /**
    * Fetches the rows for a select with fixed criteria given as two maps (a
    * criteriaMap whose values and up in the returned rows, and a joinMap for
    * other criteria).
    */
    protected List<Row> getSelectRows(final String tableName, final SQLInfoSelect select, final Map<String, Serializable> criteriaMap,
            Map<String, Serializable> joinMap, final boolean limitToOne) throws StorageException {
        final List<Row> list = new LinkedList<Row>();
        if (select.whatColumns.isEmpty()) {
            // happens when we fetch a fragment whose columns are all opaque
            // check it's a by-id query
            if (select.whereColumns.size() == 1 && select.whereColumns.get(0).getKey() == model.MAIN_KEY && joinMap == null) {
                final Row row = new Row(tableName, criteriaMap);
                // if (select.opaqueColumns != null) {
                // for (Column column : select.opaqueColumns) {
                // map.put(column.getKey(), Row.OPAQUE);
                // }
                // }
                list.add(row);
                return list;
            }
            // else do a useless select but the criteria are more complex and we
            // can't shortcut
        }
        if (joinMap == null) {
            joinMap = Collections.emptyMap();
        }
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(select.sql);

            /*
            * Compute where part.
            */
            List<Serializable> debugValues = null;
            if (logger.isLogEnabled()) {
                debugValues = new LinkedList<Serializable>();
            }
            int i = 1;
            for (final Column column : select.whereColumns) {
                final String key = column.getKey();
                Serializable v;
                if (criteriaMap.containsKey(key)) {
                    v = criteriaMap.get(key);
                } else if (joinMap.containsKey(key)) {
                    v = joinMap.get(key);
                } else {
                    throw new RuntimeException(key);
                }
                if (v == null) {
                    throw new StorageException("Null value for key: " + key);
                }
                if (v instanceof Collection<?>) {
                    // allow insert of several values, for the IN (...) case
                    for (final Object vv : (Collection<?>) v) {
                        column.setToPreparedStatement(ps, i++, (Serializable) vv);
                        if (debugValues != null) {
                            debugValues.add((Serializable) vv);
                        }
                    }
                } else {
                    column.setToPreparedStatement(ps, i++, v);
                    if (debugValues != null) {
                        debugValues.add(v);
                    }
                }
            }
            if (debugValues != null) {
                logger.logSQL(select.sql, debugValues);
            }

            /*
            * Execute query.
            */
            final ResultSet rs = ps.executeQuery();

            /*
            * Construct the maps from the result set.
            */
            while (rs.next()) {
                final Row row = new Row(tableName, criteriaMap);
                i = 1;
                for (final Column column : select.whatColumns) {
                    row.put(column.getKey(), column.getFromResultSet(rs, i++));
                }
                // if (select.opaqueColumns != null) {
                // for (Column column : select.opaqueColumns) {
                // row.putNew(column.getKey(), Row.OPAQUE);
                // }
                // }
                if (logger.isLogEnabled()) {
                    logger.logResultSet(rs, select.whatColumns);
                }
                list.add(row);
                if (limitToOne) {
                    return list;
                }
            }
            if (limitToOne) {
                return null;
            }
            return list;
        } catch (final Exception e) {
            checkConnectionReset(e, true);
            throw new StorageException("Could not select: " + select.sql, e);
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void write(final RowBatch batch) throws StorageException {
        if (!batch.creates.isEmpty()) {
            writeCreates(batch.creates);
        }
        if (!batch.updates.isEmpty()) {
            writeUpdates(batch.updates);
        }
        if (!batch.deletes.isEmpty()) {
            writeDeletes(batch.deletes);
        }
    }

    protected void writeCreates(final List<Row> creates) throws StorageException {
        // reorganize by table
        final Map<String, List<Row>> tableRows = new LinkedHashMap<String, List<Row>>();
        // hierarchy table first because there are foreign keys to it
        tableRows.put(model.HIER_TABLE_NAME, new LinkedList<Row>());
        for (final Row row : creates) {
            List<Row> rows = tableRows.get(row.tableName);
            if (rows == null) {
                tableRows.put(row.tableName, rows = new LinkedList<Row>());
            }
            rows.add(row);
        }
        // inserts on each table
        for (final Entry<String, List<Row>> en : tableRows.entrySet()) {
            final String tableName = en.getKey();
            final List<Row> rows = en.getValue();
            if (model.isCollectionFragment(tableName)) {
                    insertCollectionRows(tableName, rows);
            } else {
                for (final Row row : rows) {
                    // TODO optimize loop
                    insertSimpleRow(row);
                }
            }
        }
    }

    protected void writeUpdates(final Set<RowUpdate> updates) throws StorageException {
        // reorganize by table
        final Map<String, List<RowUpdate>> tableRows = new HashMap<String, List<RowUpdate>>();
        for (final RowUpdate rowu : updates) {
            List<RowUpdate> rows = tableRows.get(rowu.row.tableName);
            if (rows == null) {
                tableRows.put(rowu.row.tableName, rows = new LinkedList<RowUpdate>());
            }
            rows.add(rowu);
        }
        // updates on each table
        for (final Entry<String, List<RowUpdate>> en : tableRows.entrySet()) {
            final String tableName = en.getKey();
            final List<RowUpdate> rows = en.getValue();
            if (model.isCollectionFragment(tableName)) {
            	updateCollectionRows(tableName, rows);
            } else {
                for (final RowUpdate rowu : rows) {
                    // TODO optimize loop
                    updateSimpleRow(rowu.row, rowu.keys);
                }
            }
        }
    }

    protected void writeDeletes(final Collection<RowId> deletes) throws StorageException {
        // reorganize by table
        final Map<String, Set<Serializable>> tableIds = new HashMap<String, Set<Serializable>>();
        for (final RowId rowId : deletes) {
            Set<Serializable> ids = tableIds.get(rowId.tableName);
            if (ids == null) {
                tableIds.put(rowId.tableName, ids = new HashSet<Serializable>());
            }
            ids.add(rowId.id);
        }
        // delete on each table
        for (final Entry<String, Set<Serializable>> en : tableIds.entrySet()) {
            final String tableName = en.getKey();
            final Set<Serializable> ids = en.getValue();
            for (final Serializable id : ids) {
                // TODO optimize loop
                deleteRows(tableName, id);
            }
        }
    }

    protected void insertSimpleRow(final Row row) throws StorageException {
        PreparedStatement ps = null;
        try {
            // insert the row
            // XXX statement should be already prepared
            final String sql = sqlInfo.getInsertSql(row.tableName);
            if (sql == null) {
                throw new StorageException("Unknown table: " + row.tableName);
            }
            final List<Column> columns = sqlInfo.getInsertColumns(row.tableName);
            try {
                if (logger.isLogEnabled()) {
                    logger.logSQL(sql, columns, row);
                }
                ps = connection.prepareStatement(sql);
                int i = 1;
                for (final Column column : columns) {
                    column.setToPreparedStatement(ps, i++, row.get(column.getKey()));
                }
                ps.execute();
            } catch (final Exception e) {
                checkConnectionReset(e);
                throw new StorageException("Could not insert: " + sql, e);
            }
            // TODO DB_IDENTITY : post insert fetch idrow
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        // return row.id;
    }

    protected void insertCollectionRows(String tableName, final List<Row> rows) throws StorageException {
    	PreparedStatement ps = null;
        try {
            final String sql = sqlInfo.getInsertSql(tableName);
            final List<Column> columns = sqlInfo.getInsertColumns(tableName);
            try {
                List<Serializable> debugValues = null;
                if (logger.isLogEnabled()) {
                    debugValues = new ArrayList<Serializable>(3);
                }
                ps = connection.prepareStatement(sql);
                
                getCollectionIO(tableName).setToPreparedStatement(rows, columns, ps, model, debugValues, sql, logger);
                
            } catch (final Exception e) {
                checkConnectionReset(e);
                throw new StorageException("Could not insert: " + sql, e);
            }
            // TODO DB_IDENTITY : post insert fetch idrow
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    	
    }
    
    protected void insertCollectionRows(final Row row) throws StorageException {
        PreparedStatement ps = null;
        try {
            final String sql = sqlInfo.getInsertSql(row.tableName);
            final List<Column> columns = sqlInfo.getInsertColumns(row.tableName);
            try {
                List<Serializable> debugValues = null;
                if (logger.isLogEnabled()) {
                    debugValues = new ArrayList<Serializable>(3);
                }
                ps = connection.prepareStatement(sql);
                getCollectionIO(row.tableName).setToPreparedStatement(row.id, row.values, columns, ps, model, debugValues, sql, logger);
            } catch (final Exception e) {
                checkConnectionReset(e);
                throw new StorageException("Could not insert: " + sql, e);
            }
            // TODO DB_IDENTITY : post insert fetch idrow
        } finally {
            if (ps != null) {
                try {
                    closeStatement(ps);
                } catch (final SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    protected void updateSimpleRow(final Row row, final Collection<String> keys) throws StorageException {
        if (keys.isEmpty()) {
            return;
        }
        final SQLInfoSelect update = sqlInfo.getUpdateById(row.tableName, keys);
        try {
            final PreparedStatement ps = connection.prepareStatement(update.sql);
            try {
                if (logger.isLogEnabled()) {
                    logger.logSQL(update.sql, update.whatColumns, row);
                }
                int i = 1;
                for (final Column column : update.whatColumns) {
                    column.setToPreparedStatement(ps, i++, row.get(column.getKey()));
                }
                final int count = ps.executeUpdate();
                logger.logCount(count);
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not update: " + update.sql, e);
        }
    }

    protected void updateCollectionRows(String tableName, List<RowUpdate> rows) throws StorageException {
    	 List<Row> rowlist = new ArrayList<Row>();
    	 List<Serializable> idlist = new ArrayList<Serializable>();
    	 for (final RowUpdate rowu : rows) {    		 
    		 rowlist.add(rowu.row);
    		 idlist.add(rowu.row.id);
    	 }
    	 
    	 
    	 final int chunkSize = sqlInfo.getMaximumArgsForIn();
         if (idlist.size() > chunkSize) {
             for (int start = 0; start < idlist.size(); start += chunkSize) {
                 int end = start + chunkSize;
                 if (end > idlist.size()) {
                     end = idlist.size();
                 }
                 final List<Serializable> chunkIds = new ArrayList<Serializable>(idlist.subList(start, end));
                 deleteRows(tableName, chunkIds);
             }
         } else {
        	 deleteRows(tableName, idlist);
         }
    	 insertCollectionRows(tableName, rowlist);         
    }
    
    protected void updateCollectionRows(final Row row) throws StorageException {
        deleteRows(row.tableName, row.id);
        insertCollectionRows(row);
    }

    protected void deleteRows(final String tableName, final List<Serializable> ids) throws StorageException {
        try {
            final String sql = sqlInfo.getMultiDeleteSql(tableName, ids.size());
            if (logger.isLogEnabled()) {
                logger.logSQL(sql, ids);
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                for(int i = 0; i < ids.size(); ++i){
                	ps.setObject(i+1, ids.get(i));
                }
                final int count = ps.executeUpdate();
                logger.logCount(count);
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not delete: " + ids, e);
        }
    }
    
    protected void deleteRows(final String tableName, final Serializable id) throws StorageException {
        try {
            final String sql = sqlInfo.getDeleteSql(tableName);
            if (logger.isLogEnabled()) {
                logger.logSQL(sql, Collections.singletonList(id));
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                ps.setObject(1, id);
                final int count = ps.executeUpdate();
                logger.logCount(count);
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not delete: " + id.toString(), e);
        }
    }

    @Override
    public Row readSimpleRow(final RowId rowId) throws StorageException {
        final SQLInfoSelect select = sqlInfo.selectFragmentById.get(rowId.tableName);
        final Map<String, Serializable> criteriaMap = Collections.singletonMap(model.MAIN_KEY, rowId.id);
        final List<Row> maps = getSelectRows(rowId.tableName, select, criteriaMap, null, true);
        return maps == null ? null : maps.get(0);
    }

    @Override
    public Serializable[] readCollectionRowArray(final RowId rowId) throws StorageException {
        final String tableName = rowId.tableName;
        final Serializable id = rowId.id;
        final String sql = sqlInfo.selectFragmentById.get(tableName).sql;
        try {
            // XXX statement should be already prepared
            if (logger.isLogEnabled()) {
                logger.logSQL(sql, Collections.singletonList(id));
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                final List<Column> columns = sqlInfo.selectFragmentById.get(tableName).whatColumns;
                ps.setObject(1, id); // assumes only one primary column
                final ResultSet rs = ps.executeQuery();

                // construct the resulting collection using each row
                final CollectionIO io = getCollectionIO(tableName);
                final List<Serializable> list = new ArrayList<Serializable>();
                final Serializable[] returnId = new Serializable[1];
                final int[] returnPos = { -1 };
                while (rs.next()) {
                    list.add(io.getCurrentFromResultSet(rs, columns, model, returnId, returnPos));
                }
                final PropertyType type = model.getCollectionFragmentType(tableName).getArrayBaseType();
                final Serializable[] array = type.collectionToArray(list);

                if (logger.isLogEnabled()) {
                    logger.log(" -> " + Arrays.asList(array));
                }
                return array;
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not select: " + sql, e);
        }
    }

    @Override
    public Row readChildHierRow(final Serializable parentId, final String childName, final boolean complexProp) throws StorageException {
        final String sql = sqlInfo.getSelectByChildNameSql(complexProp);
        try {
            // XXX statement should be already prepared
            List<Serializable> debugValues = null;
            if (logger.isLogEnabled()) {
                debugValues = new ArrayList<Serializable>(2);
            }
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                // compute where part
                int i = 0;
                for (final Column column : sqlInfo.getSelectByChildNameWhereColumns(complexProp)) {
                    i++;
                    final String key = column.getKey();
                    Serializable v;
                    if (key.equals(model.HIER_PARENT_KEY)) {
                        v = parentId;
                    } else if (key.equals(model.HIER_CHILD_NAME_KEY)) {
                        v = childName;
                    } else {
                        throw new RuntimeException("Invalid hier column: " + key);
                    }
                    if (v == null) {
                        throw new IllegalStateException("Null value for key: " + key);
                    }
                    column.setToPreparedStatement(ps, i, v);
                    if (debugValues != null) {
                        debugValues.add(v);
                    }
                }
                if (debugValues != null) {
                    logger.logSQL(sql, debugValues);
                }
                final ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    // no match, row doesn't exist
                    return null;
                }
                // construct the row from the results
                final Row row = new Row(model.HIER_TABLE_NAME, (Serializable) null);
                i = 1;
                final List<Column> columns = sqlInfo.getSelectByChildNameWhatColumns(complexProp);
                for (final Column column : columns) {
                    row.put(column.getKey(), column.getFromResultSet(rs, i++));
                }
                row.put(model.HIER_PARENT_KEY, parentId);
                row.put(model.HIER_CHILD_NAME_KEY, childName);
                row.put(model.HIER_CHILD_ISPROPERTY_KEY, Boolean.valueOf(complexProp));
                if (logger.isLogEnabled()) {
                    logger.logResultSet(rs, columns);
                }
                // check that we didn't get several rows
                while (rs.next()) {
                    // detected a duplicate name, which means that user code
                    // wasn't careful enough. We can't go back but at least we
                    // can make the duplicate available under a different name.
                    final String newName = childName + '.' + System.currentTimeMillis();
                    i = 0;
                    Serializable childId = null;
                    for (final Column column : columns) {
                        i++;
                        if (column.getKey().equals(model.MAIN_KEY)) {
                            childId = column.getFromResultSet(rs, i);
                        }
                    }
                    logger.error(String.format("Child '%s' appeared twice as child of %s " + "(%s and %s), renaming second to '%s'", childName,
                            parentId, row.id, childId, newName));
                    final Row rename = new Row(model.HIER_TABLE_NAME, childId);
                    rename.putNew(model.HIER_CHILD_NAME_KEY, newName);
                    updateSimpleRowWithValues(model.HIER_TABLE_NAME, rename);
                }
                return row;
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not select: " + sql, e);
        }
    }

    @Override
    public List<Row> readChildHierRows(final Serializable parentId, final boolean complexProp) throws StorageException {
        if (parentId == null) {
            throw new IllegalArgumentException("Illegal null parentId");
        }
        final SQLInfoSelect select = sqlInfo.selectChildrenByIsProperty;
        final Map<String, Serializable> criteriaMap = new HashMap<String, Serializable>();
        criteriaMap.put(model.HIER_PARENT_KEY, parentId);
        criteriaMap.put(model.HIER_CHILD_ISPROPERTY_KEY, Boolean.valueOf(complexProp));
        return getSelectRows(model.HIER_TABLE_NAME, select, criteriaMap, null, false);
    }

    @Override
    public List<Row> getVersionRows(final Serializable versionSeriesId) throws StorageException {
        final SQLInfoSelect select = sqlInfo.selectVersionsBySeries;
        final Map<String, Serializable> criteriaMap = new HashMap<String, Serializable>();
        criteriaMap.put(model.VERSION_VERSIONABLE_KEY, versionSeriesId);
        criteriaMap.put(model.MAIN_IS_VERSION_KEY, Boolean.TRUE);
        return getSelectRows(model.VERSION_TABLE_NAME, select, criteriaMap, null, false);
    }

    @Override
    public List<Row> getProxyRows(final Serializable searchId, final boolean byTarget, final Serializable parentId) throws StorageException {
        final Map<String, Serializable> criteriaMap = Collections.singletonMap(byTarget ? model.PROXY_TARGET_KEY : model.PROXY_VERSIONABLE_KEY,
                searchId);
        SQLInfoSelect select;
        Map<String, Serializable> joinMap;
        if (parentId == null) {
            select = byTarget ? sqlInfo.selectProxiesByTarget : sqlInfo.selectProxiesBySeries;
            joinMap = null;
        } else {
            select = byTarget ? sqlInfo.selectProxiesByTargetAndParent : sqlInfo.selectProxiesByVersionSeriesAndParent;
            joinMap = Collections.singletonMap(model.HIER_PARENT_KEY, parentId);
        }
        return getSelectRows(model.PROXY_TABLE_NAME, select, criteriaMap, joinMap, false);
    }

    @Override
    public CopyHierarchyResult copyHierarchy(final IdWithTypes source, final Serializable destParentId, final String destName, final Row overwriteRow)
            throws StorageException {
        // assert !model.separateMainTable; // other case not implemented
        final Invalidations invalidations = new Invalidations();
        try {
            final Map<Serializable, Serializable> idMap = new LinkedHashMap<Serializable, Serializable>();
            final Map<Serializable, IdWithTypes> idToTypes = new HashMap<Serializable, IdWithTypes>();
            // copy the hierarchy fragments recursively
            final Serializable overwriteId = overwriteRow == null ? null : overwriteRow.id;
            if (overwriteId != null) {
                // overwrite hier root with explicit values
                final String tableName = model.HIER_TABLE_NAME;
                updateSimpleRowWithValues(tableName, overwriteRow);
                idMap.put(source.id, overwriteId);
                // invalidate
                invalidations.addModified(new RowId(tableName, overwriteId));
            }
            // create the new hierarchy by copy
            final Serializable newRootId = copyHierRecursive(source, destParentId, destName, overwriteId, idMap, idToTypes);
            // invalidate children
            final Serializable invalParentId = overwriteId == null ? destParentId : overwriteId;
            if (invalParentId != null) { // null for a new version
                invalidations.addModified(new RowId(Invalidations.PARENT, invalParentId));
            }
            // copy all collected fragments
            for (final Entry<String, Set<Serializable>> entry : model.getPerFragmentIds(idToTypes).entrySet()) {
                final String tableName = entry.getKey();
                final Set<Serializable> ids = entry.getValue();
                // boolean overwrite = overwriteId != null
                // && !tableName.equals(model.hierTableName);
                // overwrite ? overwriteId : null
                final Boolean invalidation = copyRows(tableName, ids, idMap, overwriteId);
                // TODO XXX check code:
                if (invalidation != null) {
                    // overwrote something
                    // make sure things are properly invalidated in this and
                    // other sessions
                    if (Boolean.TRUE.equals(invalidation)) {
                        invalidations.addModified(new RowId(tableName, overwriteId));
                    } else {
                        invalidations.addDeleted(new RowId(tableName, overwriteId));
                    }
                }
            }
            return new CopyHierarchyResult(newRootId, invalidations);
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not copy: " + source.id.toString(), e);
        }
    }

    /**
    * Updates a row in the database with given explicit values.
    */
    protected void updateSimpleRowWithValues(final String tableName, final Row row) throws StorageException {
        final Update update = sqlInfo.getUpdateByIdForKeys(tableName, row.getKeys());
        final Table table = update.getTable();
        final String sql = update.getStatement();
        try {
            final PreparedStatement ps = connection.prepareStatement(sql);
            try {
                if (logger.isLogEnabled()) {
                    final List<Serializable> values = new LinkedList<Serializable>();
                    values.addAll(row.getValues());
                    values.add(row.id); // id last in SQL
                    logger.logSQL(sql, values);
                }
                int i = 1;
                final List<String> keys = row.getKeys();
                final List<Serializable> values = row.getValues();
                final int size = keys.size();
                for (int r = 0; r < size; r++) {
                    final String key = keys.get(r);
                    final Serializable value = values.get(r);
                    table.getColumn(key).setToPreparedStatement(ps, i++, value);
                }
                ps.setObject(i, row.id); // id last in SQL
                final int count = ps.executeUpdate();
                logger.logCount(count);
            } finally {
                closeStatement(ps);
            }
        } catch (final Exception e) {
            checkConnectionReset(e);
            throw new StorageException("Could not update: " + sql, e);
        }
    }

    /**
    * Copies hierarchy from id to parentId, and recurses.
    * <p>
    * If name is {@code null}, then the original name is kept.
    * <p>
    * {@code idMap} is filled with info about the correspondence between
    * original and copied ids. {@code idType} is filled with the type of each
    * (source) fragment.
    * <p>
    * TODO: this should be optimized to use a stored procedure.
    *
    * @param overwriteId when not {@code null}, the copy is done onto this
    * existing node (skipped)
    * @return the new root id
    */
    protected Serializable copyHierRecursive(final IdWithTypes source, final Serializable parentId, final String name,
            final Serializable overwriteId, final Map<Serializable, Serializable> idMap, final Map<Serializable, IdWithTypes> idToTypes)
            throws SQLException {
        idToTypes.put(source.id, source);
        Serializable newId;
        if (overwriteId == null) {
            newId = copyHier(source.id, parentId, name, idMap);
        } else {
            newId = overwriteId;
            idMap.put(source.id, newId);
        }
        // recurse in children
        final boolean onlyComplex = parentId == null;
        for (final IdWithTypes child : getChildrenIdsWithTypes(source.id, onlyComplex)) {
            copyHierRecursive(child, newId, null, null, idMap, idToTypes);
        }
        return newId;
    }

    /**
    * Copies hierarchy from id to a new child of parentId.
    * <p>
    * If name is {@code null}, then the original name is kept.
    * <p>
    * {@code idMap} is filled with info about the correspondence between
    * original and copied ids. {@code idType} is filled with the type of each
    * (source) fragment.
    *
    * @return the new id
    */
    protected Serializable copyHier(final Serializable id, final Serializable parentId, final String name, final Map<Serializable, Serializable> idMap)
            throws SQLException {
        final boolean createVersion = parentId == null;
        final boolean explicitName = name != null;
        Serializable newId = null;

        final String sql = sqlInfo.getCopyHierSql(explicitName, createVersion);
        final PreparedStatement ps = connection.prepareStatement(sql);
        try {
            // TODO DB_IDENTITY
            newId = model.generateNewId();

            List<Serializable> debugValues = null;
            if (logger.isLogEnabled()) {
                debugValues = new ArrayList<Serializable>(4);
            }
            final List<Column> columns = sqlInfo.getCopyHierColumns(explicitName, createVersion);
            final Column whereColumn = sqlInfo.getCopyHierWhereColumn();
            int i = 1;
            for (final Column column : columns) {
                final String key = column.getKey();
                Serializable v;
                if (key.equals(model.HIER_PARENT_KEY)) {
                    v = parentId;
                } else if (key.equals(model.HIER_CHILD_NAME_KEY)) {
                    // present if name explicitely set (first iteration)
                    v = name;
                } else if (key.equals(model.MAIN_KEY)) {
                    // present if APP_UUID generation
                    v = newId;
                } else if (createVersion && (key.equals(model.MAIN_BASE_VERSION_KEY) || key.equals(model.MAIN_CHECKED_IN_KEY))) {
                    v = null;
                } else {
                    throw new RuntimeException(column.toString());
                }
                column.setToPreparedStatement(ps, i++, v);
                if (debugValues != null) {
                    debugValues.add(v);
                }
            }
            // last parameter is for 'WHERE "id" = ?'
            whereColumn.setToPreparedStatement(ps, i, id);
            if (debugValues != null) {
                debugValues.add(id);
                logger.logSQL(sql, debugValues);
            }
            final int count = ps.executeUpdate();
            logger.logCount(count);

            // TODO DB_IDENTITY
            // post insert fetch idrow

            idMap.put(id, newId);
        } finally {
            closeStatement(ps);
        }
        return newId;
    }

    /**
    * Gets the children ids and types of a node.
    */
    protected List<IdWithTypes> getChildrenIdsWithTypes(final Serializable id, final boolean onlyComplex) throws SQLException {
        final List<IdWithTypes> children = new LinkedList<IdWithTypes>();
        final String sql = sqlInfo.getSelectChildrenIdsAndTypesSql(onlyComplex);
        if (logger.isLogEnabled()) {
            logger.logSQL(sql, Collections.singletonList(id));
        }
        final List<Column> columns = sqlInfo.getSelectChildrenIdsAndTypesWhatColumns();
        final PreparedStatement ps = connection.prepareStatement(sql);
        try {
            List<String> debugValues = null;
            if (logger.isLogEnabled()) {
                debugValues = new LinkedList<String>();
            }
            ps.setObject(1, id); // parent id
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Serializable childId = null;
                String childPrimaryType = null;
                String[] childMixinTypes = null;
                int i = 1;
                for (final Column column : columns) {
                    final String key = column.getKey();
                    final Serializable value = column.getFromResultSet(rs, i++);
                    if (key.equals(model.MAIN_KEY)) {
                        childId = value;
                    } else if (key.equals(model.MAIN_PRIMARY_TYPE_KEY)) {
                        childPrimaryType = (String) value;
                    } else if (key.equals(model.MAIN_MIXIN_TYPES_KEY)) {
                        childMixinTypes = (String[]) value;
                    }
                }
                children.add(new IdWithTypes(childId, childPrimaryType, childMixinTypes));
                if (debugValues != null) {
                    debugValues.add(childId + "/" + childPrimaryType + "/" + childMixinTypes);
                }
            }
            if (debugValues != null) {
                logger.log(" -> " + debugValues);
            }
            return children;
        } finally {
            closeStatement(ps);
        }
    }

    /**
    * Copy the rows from tableName with given ids into new ones with new ids
    * given by idMap.
    * <p>
    * A new row with id {@code overwriteId} is first deleted.
    *
    * @return {@link Boolean#TRUE} for a modification or creation,
    * {@link Boolean#FALSE} for a deletion, {@code null} otherwise
    * (still absent)
    * @throws SQLException
    */
    protected Boolean copyRows(final String tableName, final Set<Serializable> ids, final Map<Serializable, Serializable> idMap,
            final Serializable overwriteId) throws SQLException {
        final String copySql = sqlInfo.getCopySql(tableName);
        final Column copyIdColumn = sqlInfo.getCopyIdColumn(tableName);
        final PreparedStatement copyPs = connection.prepareStatement(copySql);
        final String deleteSql = sqlInfo.getDeleteSql(tableName);
        final PreparedStatement deletePs = connection.prepareStatement(deleteSql);
        try {
            boolean before = false;
            boolean after = false;
            for (final Serializable id : ids) {
                final Serializable newId = idMap.get(id);
                final boolean overwrite = newId.equals(overwriteId);
                if (overwrite) {
                    // remove existing first
                    if (logger.isLogEnabled()) {
                        logger.logSQL(deleteSql, Collections.singletonList(newId));
                    }
                    deletePs.setObject(1, newId);
                    final int delCount = deletePs.executeUpdate();
                    logger.logCount(delCount);
                    before = delCount > 0;
                }
                copyIdColumn.setToPreparedStatement(copyPs, 1, newId);
                copyIdColumn.setToPreparedStatement(copyPs, 2, id);
                if (logger.isLogEnabled()) {
                    logger.logSQL(copySql, Arrays.asList(newId, id));
                }
                final int copyCount = copyPs.executeUpdate();
                logger.logCount(copyCount);
                if (overwrite) {
                    after = copyCount > 0;
                }
            }
            // * , n -> mod (TRUE)
            // n , 0 -> del (FALSE)
            // 0 , 0 -> null
            return after ? Boolean.TRUE : before ? Boolean.FALSE : null;
        } finally {
            closeStatement(copyPs);
            closeStatement(deletePs);
        }
    }

}