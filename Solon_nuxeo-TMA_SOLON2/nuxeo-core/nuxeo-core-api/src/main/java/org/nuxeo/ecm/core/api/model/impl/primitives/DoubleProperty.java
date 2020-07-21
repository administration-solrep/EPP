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

package org.nuxeo.ecm.core.api.model.impl.primitives;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyConversionException;
import org.nuxeo.ecm.core.api.model.impl.ScalarProperty;
import org.nuxeo.ecm.core.schema.types.Field;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DoubleProperty extends ScalarProperty {

    private static final long serialVersionUID = 3764033209483557763L;

    public DoubleProperty(Property parent, Field field, int flags) {
        super(parent, field, flags);
    }

    @Override
    public boolean isNormalized(Object value) {
        return value == null || value.getClass() == Double.class;
    }

    @Override
    public Serializable normalize(Object value)
            throws PropertyConversionException {
        if (isNormalized(value)) {
            return (Serializable)value;
        }
        if (value.getClass() == String.class) {
            String string = (String)value;
            if (string.length() == 0) {
                return null;
            }
            return Double.valueOf(value.toString());
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        throw new PropertyConversionException(value.getClass(), Double.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Serializable value, Class<T> toType)
            throws PropertyConversionException {
        if (value == null || Double.class == toType) {
            return (T) value;
        }
        Double v = (Double) value;
        if (toType == Integer.class) {
            return (T) new Integer(v.intValue());
        }
        if (toType == String.class) {
            return (T) v.toString();
        }
        if (toType == Long.class) {
            return (T) new Long(v.longValue());
        }
        if (toType == Float.class) {
            return (T) new Float(v.floatValue());
        }
        if (toType == Short.class) {
            return (T) new Short(v.shortValue());
        }
        if (toType == Byte.class) {
            return (T) new Byte(v.byteValue());
        }
        throw new PropertyConversionException(value.getClass(), toType);
    }

    @Override
    public Object newInstance() {
        return (double) 0;
    }

}
