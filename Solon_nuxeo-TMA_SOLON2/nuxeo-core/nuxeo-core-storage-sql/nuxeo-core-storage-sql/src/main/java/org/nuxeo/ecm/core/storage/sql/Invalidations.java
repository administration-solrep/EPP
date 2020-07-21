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
import java.util.HashSet;
import java.util.Set;

/**
* A set of invalidations.
* <p>
* Records both modified and deleted fragments, as well as "parents modified"
* fragments.
*/
public class Invalidations implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Pseudo-table to use to notify about children invalidated. */
    public static final String PARENT = "__PARENT__";

    public static final int MODIFIED = 1;

    public static final int DELETED = 2;

    /** used locally when invalidating everything */
    public boolean all;

    /** null when empty */
    public Set<RowId> modified;

    /** null when empty */
    public Set<RowId> deleted;

    public Invalidations() {
    }

    public Invalidations(final boolean all) {
        this.all = all;
    }

    public boolean isEmpty() {
        return modified == null && deleted == null && !all;
    }

    public void clear() {
        all = false;
        modified = null;
        deleted = null;
    }

    /** only call this if it's to add at least one element in the set */
    public Set<RowId> getKindSet(final int kind) {
        switch (kind) {
        case MODIFIED:
            if (modified == null) {
                modified = new HashSet<RowId>();
            }
            return modified;
        case DELETED:
            if (deleted == null) {
                deleted = new HashSet<RowId>();
            }
            return deleted;
        }
        throw new AssertionError();
    }

    public void add(final Invalidations other) {
        if (other == null) {
            return;
        }
        if (all) {
            return;
        }
        if (other.all) {
            all = true;
            modified = null;
            deleted = null;
            return;
        }
        if (other.modified != null) {
            addModified(other.modified);
        }
        if (other.deleted != null) {
            addDeleted(other.deleted);
        }
    }

    public void addModified(final RowId rowId) {
        if (all) {
            return;
        }
        if (modified == null) {
            modified = new HashSet<RowId>();
        }
        modified.add(rowId);
    }

    protected void addModified(final Set<RowId> rowIds) {
        if (modified == null) {
            modified = new HashSet<RowId>();
        }
        modified.addAll(rowIds);
    }

    public void addDeleted(final RowId rowId) {
        if (all) {
            return;
        }
        if (deleted == null) {
            deleted = new HashSet<RowId>();
        }
        deleted.add(rowId);
    }

    protected void addDeleted(final Set<RowId> rowIds) {
        if (deleted == null) {
            deleted = new HashSet<RowId>();
        }
        deleted.addAll(rowIds);
    }

    public void add(final Serializable id, final String[] tableNames, final int kind) {
        if (tableNames.length == 0) {
            return;
        }
        final Set<RowId> set = getKindSet(kind);
        for (final String tableName : tableNames) {
            set.add(new RowId(tableName, id));
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + '(');
        if (all) {
            sb.append("all=true");
        }
        if (modified != null) {
            sb.append("modified=");
            sb.append(modified);
            if (deleted != null) {
                sb.append(',');
            }
        }
        if (deleted != null) {
            sb.append("deleted=");
            sb.append(deleted);
        }
        sb.append(')');
        return sb.toString();
    }

    public static final class InvalidationsPair implements Serializable {

        private static final long serialVersionUID = 1L;

        public final Invalidations cacheInvalidations;

        public final Invalidations eventInvalidations;

        public InvalidationsPair(final Invalidations cacheInvalidations, final Invalidations eventInvalidations) {
            this.cacheInvalidations = cacheInvalidations;
            this.eventInvalidations = eventInvalidations;
        }
    }

}
