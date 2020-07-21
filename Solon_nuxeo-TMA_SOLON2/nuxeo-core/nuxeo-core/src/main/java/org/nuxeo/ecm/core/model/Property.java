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

package org.nuxeo.ecm.core.model;

import java.util.Collection;
import java.util.Iterator;

import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.schema.types.Type;

/**
 * This class describes a document property.
 * <p>
 * Properties are lazily created.
 * This means a property can be retrieved
 * - using <code>getProperty(name)</code>) on the container -
 * even if it not already exists but was defined by the container schema.
 * <p>
 * The first time a write operation is invoked on such a property,
 * the property will be created by the underlying storage.
 *
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface Property {

    /**
     * Gets the name used to identify this property by its container.
     * <p>
     * If the container of this property is not supporting accessing properties by
     * names, returns the empty string.
     *
     * @return the object name or the empty string if the object has no name
     * @throws DocumentException if the type cannot be resolved
     */
    String getName() throws DocumentException;

    /**
     * Gets the property type.
     *
     * @return the property type
     */
    Type getType();

    /**
     * Gets the value of this property.
     *
     * @return this property's value
     * @throws DocumentException
     */
    Object getValue() throws DocumentException;

    /**
     * Sets the value of this property.
     *
     * @param value the value to set
     * @throws DocumentException
     */
    void setValue(Object value) throws DocumentException;

    /**
     * Tests whether or not this property is null.
     * <p>
     * A null property means that it is defined by the container schema
     * but was not yet set (so it may not exists as a persistent object)
     * <p>
     * If the property is null the first time a write operation is done
     * on it the property will be created by the underlying storage
     *
     * @return true if the property is null, false otherwise
     */
    boolean isNull();

    /**
     * Nullify this property.
     * <p>
     * The property is set to null which may result in being removed from the storage
     * (this aspect depends on the implementation)
     *
     * @throws DocumentException
     */
    void setNull() throws DocumentException;

    /**
     * Checks whether this property has child property with the given name.
     *
     * @return true if this property has children properties, false otherwise
     * @throws DocumentException
     * @throws UnsupportedOperationException if this is not a composite property
     */
    boolean isPropertySet(String name) throws DocumentException;

    /**
     * Gets the children property given its name.
     *
     * @param name the property name
     * @return the property
     * @throws DocumentException
     * @throws UnsupportedOperationException if this is not a composite property
     */
    Property getProperty(String name) throws DocumentException;

    /**
     * Gets the collection of the children properties.
     *
     * @return an iterator over the children properties
     * @throws DocumentException
     * @throws UnsupportedOperationException if this is not a composite property
     */
    Collection<Property> getProperties() throws DocumentException;

    /**
     * Gets an iterator over the children properties.
     *
     * @return an iterator over the children properties
     * @throws DocumentException
     * @throws UnsupportedOperationException if this is not a composite property
     */
    Iterator<Property> getPropertyIterator() throws DocumentException;

}
