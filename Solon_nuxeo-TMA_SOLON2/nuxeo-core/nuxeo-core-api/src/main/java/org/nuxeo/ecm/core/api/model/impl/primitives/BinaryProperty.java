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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyConversionException;
import org.nuxeo.ecm.core.api.model.PropertyError;
import org.nuxeo.ecm.core.api.model.impl.ScalarProperty;
import org.nuxeo.ecm.core.schema.types.Field;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class BinaryProperty extends ScalarProperty {

    private static final long serialVersionUID = 1036197257646828836L;

    public BinaryProperty(Property parent, Field field, int flags) {
        super(parent, field, flags);
    }

    @Override
    public boolean isNormalized(Object value) {
        return value == null || value instanceof InputStream;
    }

    @Override
    public Serializable normalize(Object value)
            throws PropertyConversionException {
        if (isNormalized(value)) {
            //TODO if input stream is not serializable? do we convert to a serializable input stream?
            return (Serializable)value;
        }
        throw new PropertyConversionException(value.getClass(), InputStream.class);
        //TODO byte array is not serializable
//        if (value.getClass() == String.class) {
//            return new ByteArrayInputStream(((String)value).getBytes());
//        }
//        if (value.getClass() == byte[].class) {
//            return new ByteArrayInputStream((byte[])value.);
//        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Serializable value, Class<T> toType)
            throws PropertyConversionException {
        if (value == null) {
            return null;
        }
        if (InputStream.class.isAssignableFrom(toType)) {
            return (T) value;
        }
        if (toType == String.class) {
            try {
                return (T) FileUtils.read((InputStream) value);
            } catch (IOException e) {
                throw new PropertyError("Failed to read given input stream", e);
            }
        }
        if (toType == byte[].class) {
            try {
                return (T) FileUtils.readBytes((InputStream) value);
            } catch (IOException e) {
                throw new PropertyError("Failed to read given input stream", e);
            }
        }
        throw new PropertyConversionException(value.getClass(), toType);
    }

    @Override
    public Object newInstance() {
        return new ByteArrayInputStream("".getBytes()); // TODO not serializable
    }

}
