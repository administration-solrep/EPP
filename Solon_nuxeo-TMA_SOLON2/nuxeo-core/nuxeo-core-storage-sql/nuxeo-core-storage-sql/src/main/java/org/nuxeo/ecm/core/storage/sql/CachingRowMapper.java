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
package org.nuxeo.ecm.core.storage.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.apache.commons.collections.map.ReferenceMap;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.ACLRow.ACLRowPositionComparator;
import org.nuxeo.ecm.core.storage.sql.Invalidations.InvalidationsPair;

/**
* A {@link RowMapper} that has an internal cache.
* <p>
* The cache only holds {@link Row}s that are known to be identical to what's in
* the underlying {@link RowMapper}.
*/
public class CachingRowMapper implements RowMapper {

    private static final String ABSENT = "__ABSENT__\0\0\0";

    /**
    * The cached rows. All held data is identical to what is present in the
    * underlying {@link RowMapper} and could be refetched if needed.
    * <p>
    * The values are either {@link Row} for fragments present in the database,
    * or a row with tableName {@link #ABSENT} to denote a fragment known to be
    * absent from the database.
    * <p>
    * This cache is memory-sensitive (all values are soft-referenced), a
    * fragment can always be refetched if the GC collects it.
    */
    // we use a new Row instance for the absent case to avoid keeping other
    // references to it which would prevent its GCing
    private final Map<RowId, Row> cache;

    /**
    * The {@link RowMapper} to which operations that cannot be processed from
    * the cache are delegated.
    */
    private final RowMapper rowMapper;

    /**
    * The local invalidations due to writes through this mapper that should be
    * propagated to other sessions at post-commit time.
    */
    private final Invalidations localInvalidations;

    /**
    * The queue of cache invalidations received from other session, to process
    * at pre-transaction time.
    */
    private final InvalidationsQueue cacheQueue;

    /**
    * The propagator of invalidations to other mappers.
    */
    private final InvalidationsPropagator cachePropagator;

    /**
    * The queue of invalidations used for events, a single queue is shared by
    * all mappers corresponding to the same client repository.
    */
    private InvalidationsQueue eventQueue;

    /**
    * The propagator of event invalidations to all event queues.
    */
    private final InvalidationsPropagator eventPropagator;

    /**
    * The session, used for event propagation.
    */
    private SessionImpl session;

    protected boolean forRemoteClient;

    @SuppressWarnings("unchecked")
    public CachingRowMapper(final RowMapper rowMapper, final InvalidationsPropagator cachePropagator, final InvalidationsPropagator eventPropagator,
            final InvalidationsQueue repositoryEventQueue) {
        this.rowMapper = rowMapper;
        cache = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);
        localInvalidations = new Invalidations();
        cacheQueue = new InvalidationsQueue("mapper-" + this);
        this.cachePropagator = cachePropagator;
        cachePropagator.addQueue(cacheQueue); // TODO when to remove?
        eventQueue = repositoryEventQueue;
        this.eventPropagator = eventPropagator;
        eventPropagator.addQueue(repositoryEventQueue);
        forRemoteClient = false;
    }

    /*
    * ----- Cache -----
    */

    protected static boolean isAbsent(final Row row) {
        return row.tableName == ABSENT; // == is ok
    }

    protected void cachePut(Row row) {
        row = row.clone();
        // for ACL collections, make sure the order is correct
        // (without the cache, the query to get a list of collection does an
        // ORDER BY pos, so users of the cache must get the same behavior)
        if (row.isCollection() && row.values.length > 0 && row.values[0] instanceof ACLRow) {
            row.values = sortACLRows((ACLRow[]) row.values);
        }
        cache.put(new RowId(row), row);
    }

    protected ACLRow[] sortACLRows(final ACLRow[] acls) {
        final List<ACLRow> list = new ArrayList<ACLRow>(Arrays.asList(acls));
        Collections.sort(list, ACLRowPositionComparator.INSTANCE);
        final ACLRow[] res = new ACLRow[acls.length];
        return list.toArray(res);
    }

    protected void cachePutAbsent(final RowId rowId) {
        cache.put(new RowId(rowId), new Row(ABSENT, (Serializable) null));
    }

    protected void cachePutAbsentIfNull(final RowId rowId, final Row row) {
        if (row != null) {
            cachePut(row);
        } else {
            cachePutAbsent(rowId);
        }
    }

    protected void cachePutAbsentIfRowId(final RowId rowId) {
        if (rowId instanceof Row) {
            cachePut((Row) rowId);
        } else {
            cachePutAbsent(rowId);
        }
    }

    protected Row cacheGet(final RowId rowId) {
        Row row = cache.get(rowId);
        if (row != null && !isAbsent(row)) {
            row = row.clone();
        }
        return row;
    }

    protected void cacheRemove(final RowId rowId) {
        cache.remove(rowId);
    }

    /*
    * ----- Invalidations / Cache Management -----
    */

    @Override
    public InvalidationsPair receiveInvalidations() throws StorageException {
        // invalidations from the underlying mapper (remote, cluster)
        final InvalidationsPair invals = rowMapper.receiveInvalidations();

        // add local accumulated invalidations to remote ones
        final Invalidations invalidations = cacheQueue.getInvalidations();
        if (invals != null) {
            invalidations.add(invals.cacheInvalidations);
        }

        // add local accumulated events to remote ones
        final Invalidations events = eventQueue.getInvalidations();
        if (invals != null) {
            events.add(invals.eventInvalidations);
        }

        // invalidate our cache
        if (invalidations.all) {
            clearCache();
        }
        if (invalidations.modified != null) {
            for (final RowId rowId : invalidations.modified) {
                cacheRemove(rowId);
            }
        }
        if (invalidations.deleted != null) {
            for (final RowId rowId : invalidations.deleted) {
                cachePutAbsent(rowId);
            }
        }

        if (invalidations.isEmpty() && events.isEmpty()) {
            return null;
        }
        return new InvalidationsPair(invalidations.isEmpty() ? null : invalidations, events.isEmpty() ? null : events);
    }

    // propagate invalidations
    @Override
    public void sendInvalidations(Invalidations invalidations) throws StorageException {
        // add local invalidations
        if (!localInvalidations.isEmpty()) {
            if (invalidations == null) {
                invalidations = new Invalidations();
            }
            invalidations.add(localInvalidations);
            localInvalidations.clear();
        }

        if (invalidations != null && !invalidations.isEmpty()) {
            // send to underlying mapper
            rowMapper.sendInvalidations(invalidations);

            // queue to other local mappers' caches
            cachePropagator.propagateInvalidations(invalidations, cacheQueue);

            // queue as events for other repositories
            eventPropagator.propagateInvalidations(invalidations, eventQueue);

            // send event to local repository (synchronous)
            // only if not the server-side part of a remote client
            if (!forRemoteClient) {
                session.sendInvalidationEvent(invalidations, true);
            }
        }
    }

    /**
    * Used by the server to associate each mapper to a single event
    * invalidations queue per client repository.
    */
    public void setEventQueue(final InvalidationsQueue eventQueue) {
        // don't remove the original global repository queue
        this.eventQueue = eventQueue;
        eventPropagator.addQueue(eventQueue);
        forRemoteClient = true;
    }

    /**
    * Sets the session, used for event propagation.
    */
    protected void setSession(final SessionImpl session) {
        this.session = session;
    }

    @Override
    public void clearCache() {
        cache.clear();
        localInvalidations.clear();
        rowMapper.clearCache();
    }

    @Override
    public void rollback(final Xid xid) throws XAException {
        try {
            rowMapper.rollback(xid);
        } finally {
            cache.clear();
            localInvalidations.clear();
        }
    }

    /*
    * ----- Batch -----
    */

    /*
    * Use those from the cache if available, read from the mapper for the rest.
    */
    @Override
    public List<? extends RowId> read(final Collection<RowId> rowIds) throws StorageException {
        final List<RowId> res = new ArrayList<RowId>(rowIds.size());
        // find which are in cache, and which not
        final List<RowId> todo = new LinkedList<RowId>();
        for (final RowId rowId : rowIds) {
            final Row row = cacheGet(rowId);
            if (row == null) {
                todo.add(rowId);
            } else if (isAbsent(row)) {
                res.add(new RowId(rowId));
            } else {
                res.add(row);
            }
        }
        // ask missing ones to underlying row mapper
        final List<? extends RowId> fetched = rowMapper.read(todo);
        // add them to the cache
        for (final RowId rowId : fetched) {
            cachePutAbsentIfRowId(rowId);
        }
        // merge results
        res.addAll(fetched);
        return res;
    }

    /*
    * Save in the cache then pass all the writes to the mapper.
    */
    @Override
    public void write(final RowBatch batch) throws StorageException {
        // we avoid gathering invalidations for a write-only table: fulltext
        for (final Row row : batch.creates) {
            cachePut(row);
            if (!Model.FULLTEXT_TABLE_NAME.equals(row.tableName)) {
                // we need to send modified invalidations for created
                // fragments because other session's ABSENT fragments have
                // to be invalidated
                localInvalidations.addModified(new RowId(row));
            }
        }
        for (final RowUpdate rowu : batch.updates) {
            cachePut(rowu.row);
            if (!Model.FULLTEXT_TABLE_NAME.equals(rowu.row.tableName)) {
                localInvalidations.addModified(new RowId(rowu.row));
            }
        }
        for (final RowId rowId : batch.deletes) {
            if (rowId instanceof Row) {
                throw new AssertionError();
            }
            cachePutAbsent(rowId);
            if (!Model.FULLTEXT_TABLE_NAME.equals(rowId.tableName)) {
                localInvalidations.addDeleted(rowId);
            }
        }

        // propagate to underlying mapper
        rowMapper.write(batch);
    }

    /*
    * ----- Read -----
    */

    @Override
    public Row readSimpleRow(final RowId rowId) throws StorageException {
        Row row = cacheGet(rowId);
        if (row == null) {
            row = rowMapper.readSimpleRow(rowId);
            cachePutAbsentIfNull(rowId, row);
            return row;
        } else if (isAbsent(row)) {
            return null;
        } else {
            return row;
        }
    }

    @Override
    public Serializable[] readCollectionRowArray(final RowId rowId) throws StorageException {
        Row row = cacheGet(rowId);
        if (row == null) {
            final Serializable[] array = rowMapper.readCollectionRowArray(rowId);
            assert array != null;
            row = new Row(rowId.tableName, rowId.id, array);
            cachePut(row);
            return row.values;
        } else if (isAbsent(row)) {
            return null;
        } else {
            return row.values;
        }
    }

    // TODO this API isn't cached well...
    @Override
    public Row readChildHierRow(final Serializable parentId, final String childName, final boolean complexProp) throws StorageException {
        final Row row = rowMapper.readChildHierRow(parentId, childName, complexProp);
        if (row != null) {
            cachePut(row);
        }
        return row;
    }

    // TODO this API isn't cached well...
    @Override
    public List<Row> readChildHierRows(final Serializable parentId, final boolean complexProp) throws StorageException {
        final List<Row> rows = rowMapper.readChildHierRows(parentId, complexProp);
        for (final Row row : rows) {
            cachePut(row);
        }
        return rows;
    }

    // TODO this API isn't cached well...
    @Override
    public List<Row> getVersionRows(final Serializable versionableId) throws StorageException {
        final List<Row> rows = rowMapper.getVersionRows(versionableId);
        for (final Row row : rows) {
            cachePut(row);
        }
        return rows;
    }

    // TODO this API isn't cached well...
    @Override
    public List<Row> getProxyRows(final Serializable searchId, final boolean byTarget, final Serializable parentId) throws StorageException {
        final List<Row> rows = rowMapper.getProxyRows(searchId, byTarget, parentId);
        for (final Row row : rows) {
            cachePut(row);
        }
        return rows;
    }

    /*
    * ----- Copy -----
    */

    @Override
    public CopyHierarchyResult copyHierarchy(final IdWithTypes source, final Serializable destParentId, final String destName, final Row overwriteRow)
            throws StorageException {
        final CopyHierarchyResult result = rowMapper.copyHierarchy(source, destParentId, destName, overwriteRow);
        final Invalidations invalidations = result.invalidations;
        if (invalidations.modified != null) {
            for (final RowId rowId : invalidations.modified) {
                cacheRemove(rowId);
                localInvalidations.addModified(new RowId(rowId));
            }
        }
        if (invalidations.deleted != null) {
            for (final RowId rowId : invalidations.deleted) {
                cacheRemove(rowId);
                localInvalidations.addDeleted(rowId);
            }
        }
        return result;
    }

}
