/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.storage.sql.jdbc.db;

import java.io.Serializable;

/**
 * A {@code SELECT} statement.
 *
 * @author Florent Guillaume
 */
public class Select implements Serializable {

    private static final long serialVersionUID = 1L;

    private String with;

    private String what;

    private String from;

    private String where;

    private String groupBy;

    private String orderBy;

    public Select(Table table) {
        // table unused
    }

    public void setWith(String with) {
        this.with = with;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getWhere() {
        return where;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getStatement() {
        StringBuilder buf = new StringBuilder(128);
        if (with != null && with.length() != 0) {
            buf.append("WITH ");
            buf.append(with);
            buf.append(' ');
        }
        buf.append("SELECT ");
        buf.append(what);
        buf.append(" FROM ");
        buf.append(from);
        if (where != null && where.length() != 0) {
            buf.append(" WHERE ");
            buf.append(where);
        }
        if (groupBy != null && groupBy.length() != 0) {
            buf.append(" GROUP BY ");
            buf.append(groupBy);
        }
        if (orderBy != null && orderBy.length() != 0) {
            buf.append(" ORDER BY ");
            buf.append(orderBy);
        }
        // ... "for update" in some cases, see dialect.getForUpdateString and
        // lock modes
        return buf.toString();
    }

}
