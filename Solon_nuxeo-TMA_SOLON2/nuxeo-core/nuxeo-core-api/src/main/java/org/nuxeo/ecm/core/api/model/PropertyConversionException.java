/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.model;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class PropertyConversionException extends InvalidPropertyValueException {

    private static final long serialVersionUID = -7766425583638251741L;

    public PropertyConversionException(Class<?> fromClass, Class<?> toClass) {
        super("Property Conversion failed from " + fromClass + " to " + toClass);
    }

    public PropertyConversionException(Class<?> fromClass, Class<?> toClass, String message) {
        super("Property Conversion failed from " + fromClass + " to " + toClass
                + ": " + message);
    }

    public PropertyConversionException(String message) {
        super(message);
    }

}
