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

package org.nuxeo.ecm.core.storage.sql.coremodel;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.ListDiff;
import org.nuxeo.ecm.core.model.Property;
import org.nuxeo.ecm.core.schema.types.ComplexType;
import org.nuxeo.ecm.core.schema.types.ListType;
import org.nuxeo.ecm.core.storage.sql.Node;

/**
 * A {@link SQLComplexListProperty} gives access to a wrapped collection of
 * SQL-level {@link Node}s.
 *
 * @author Florent Guillaume
 */
public class SQLComplexListProperty extends SQLBaseProperty {

    protected final Node node;

    protected final String name;

    protected final SQLSession session;

    protected final ComplexType elementType;

    /**
     * Creates a {@link SQLComplexListProperty} to wrap a collection of
     * {@link Node}s.
     */
    public SQLComplexListProperty(Node node, ListType type, String name,
            SQLSession session, boolean readonly) {
        super(type, name, readonly);
        this.node = node;
        this.name = name;
        this.session = session;
        elementType = (ComplexType) type.getFieldType();
    }

    /*
     * ----- org.nuxeo.ecm.core.model.Property -----
     */

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Object> getValue() throws DocumentException {
        List<Property> properties = session.makeProperties(node, name, type,
                null, readonly, -1);
        List<Object> list = new ArrayList<Object>(properties.size());
        for (Property property : properties) {
            list.add(property.getValue());
        }
        return list;
    }

    @Override
    public void setValue(Object value) throws DocumentException {
        checkWritable();
        if (value instanceof ListDiff) {
            setList((ListDiff) value);
        } else if (value instanceof List) {
            setList((List<?>) value);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported value object for a complex list: "
                            + value.getClass().getName());
        }
    }

    /*
     * ----- internal -----
     */

    public void setList(List<?> list) throws DocumentException {
        // don't add/remove nodes for unchanged complex value
        if (getValue().equals(list)) {
            return;
        }
        // remove previous nodes
        List<Node> nodes = session.getComplexList(node, name);
        for (Node n : nodes) {
            session.remove(n);
        }
        // add new nodes
        List<Property> properties = session.makeProperties(node, name, type,
                null, readonly, list.size());
        // set values
        int i = 0;
        for (Object value : list) {
            properties.get(i++).setValue(value);
        }
    }

    public void setList(ListDiff list) {
        if (!list.isDirty()) {
            return;
        }
        for (ListDiff.Entry entry : list.diff()) {
            switch (entry.type) {
            case ListDiff.ADD:
                // add(entry.value);
                break;
            case ListDiff.REMOVE:
                // remove(entry.index);
                break;
            case ListDiff.INSERT:
                // insert(entry.index, entry.value);
                break;
            case ListDiff.MOVE:
                // move(entry.index, (Integer) entry.value);
                break;
            case ListDiff.MODIFY:
                // modify(entry.index, entry.value);
                break;
            case ListDiff.CLEAR:
                // clear();
                break;
            }
        }
        throw new UnsupportedOperationException();
    }

}
