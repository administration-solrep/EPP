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

package org.nuxeo.ecm.core.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.model.impl.ListProperty;
import org.nuxeo.ecm.core.api.model.impl.MapProperty;
import org.nuxeo.ecm.core.api.model.impl.ScalarProperty;
import org.nuxeo.ecm.core.api.model.impl.primitives.BlobProperty;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@SuppressWarnings("unchecked")
public class ValueExporter implements PropertyVisitor {

    private final Map<String, Serializable> result = new HashMap<String, Serializable>();

    public Map<String, Serializable> getResult() {
        return result;
    }

    public Map<String, Serializable> run(DocumentPart dp)
            throws PropertyException {
        dp.accept(this, result);
        return result;
    }

    @Override
    public boolean acceptPhantoms() {
        return false;
    }

    @Override
    public Object visit(DocumentPart property, Object arg)
            throws PropertyException {
        return arg;
    }

    @Override
    public Object visit(MapProperty property, Object arg)
            throws PropertyException {

        Serializable value;
        if (property.isContainer()) {
            value = new HashMap<String, Serializable>();
        } else {
            value = property.getValue();
        }

        if (BlobProperty.class.isAssignableFrom(property.getClass())) {
            value = property.getValue();
            if (property.getParent().isList()) {
                ((Collection<Serializable>) arg).add(value);
            } else {
                ((Map<String, Serializable>) arg).put(property.getName(),
                        value);
            }
            return null;
        } else if (property.getParent().isList()) {
            // if (arg instanceof Collection) {
            ((Collection<Serializable>) arg).add(value);
        } else {
            ((Map<String, Serializable>) arg).put(property.getName(), value);
        }
        return value;
    }

    @Override
    public Object visit(ListProperty property, Object arg)
            throws PropertyException {
        Serializable value;
        if (property.isContainer()) {
            value = new ArrayList<Serializable>();
        } else {
            value = property.getValue();
        }
        if (property.getParent().isList()) {
            // if (arg instanceof Collection) {
            ((Collection<Serializable>) arg).add(value);
        } else {
            ((Map<String, Serializable>) arg).put(property.getName(), value);
        }
        return value;
    }

    @Override
    public Object visit(ScalarProperty property, Object arg)
            throws PropertyException {

        if (property.getParent().isList()) {
            // if (arg instanceof Collection) {
            ((Collection<Serializable>) arg).add(property.getValue());
        } else {
            ((Map<String, Serializable>) arg).put(property.getName(), property
                    .getValue());
        }
        return null;
    }

}
