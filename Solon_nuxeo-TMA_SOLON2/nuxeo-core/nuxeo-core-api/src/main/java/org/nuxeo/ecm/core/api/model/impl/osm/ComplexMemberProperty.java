/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.model.impl.osm;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.api.model.impl.MapProperty;
import org.nuxeo.ecm.core.api.model.impl.ScalarProperty;
import org.nuxeo.ecm.core.schema.types.Field;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ComplexMemberProperty extends MapProperty implements Adaptable {

    private static final long serialVersionUID = 1537310098432620929L;

    private static final Log log = LogFactory.getLog(ComplexMemberProperty.class);

    protected final ObjectAdapter adapter;

    public ComplexMemberProperty(ObjectAdapter adapter, Property parent,
            Field field) {
        super(parent, field);
        this.adapter = adapter;
    }

    public ComplexMemberProperty(ObjectAdapter adapter, Property parent,
            Field field, int flags) {
        super(parent, field, flags);
        this.adapter = adapter;
    }

    @Override
    public ObjectAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) throws PropertyException {
        if (value instanceof Map) {
            adapter.setMap(getValue(), (Map<String, Object>) value);
            setIsModified();
        } else {
            super.setValue(value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(Serializable value) throws PropertyException {
        if (value == null) {
            // IGNORE null values - properties will be
            // considered PHANTOMS
            return;
        }
        if (value instanceof Map) {
            internalSetValue((Serializable) getAdapter().create(
                    (Map<String, Object>) value));
        } else {
            internalSetValue(value);
        }
        removePhantomFlag();
    }

    @Override
    public void internalSetValue(Serializable value) throws PropertyException {
        ObjectAdapter adapter = ((Adaptable) parent).getAdapter();
        adapter.setValue(parent.getValue(), getName(), value);
    }

    @Override
    public Serializable internalGetValue() throws PropertyException {
        ObjectAdapter adapter = ((Adaptable) parent).getAdapter();
        return (Serializable) adapter.getValue(parent.getValue(), getName());
    }

    @Override
    public Serializable getValueForWrite() throws PropertyException {
        return getValue();
    }

    @Override
    protected Property internalGetChild(Field field) {
        try {
            ObjectAdapter subAdapter = getAdapter().getAdapter(
                    field.getName().getPrefixedName());
            if (subAdapter == null) { // a simple property
                return new ScalarMemberProperty(this, field,
                        isPhantom() ? IS_PHANTOM : 0);
            } else { // a complex property
                return new ComplexMemberProperty(subAdapter, this, field,
                        isPhantom() ? IS_PHANTOM : 0);
            }
        } catch (PropertyNotFoundException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    protected Serializable getDefaultValue() {
        return getAdapter().getDefaultValue();
    }

    @Override
    public boolean isSameAs(Property property) throws PropertyException {
        if (property == null) {
            return false;
        }
        ScalarProperty sp = (ScalarProperty) property;
        Object v1 = getValue();
        Object v2 = sp.getValue();
        if (v1 == null) {
            return v2 == null;
        }
        return v1.equals(v2);
    }

}
