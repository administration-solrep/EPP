/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: Layout.java 26053 2007-10-16 01:45:43Z atchertchian $
 */

package org.nuxeo.ecm.platform.forms.layout.api;

import java.io.Serializable;
import java.util.Map;

/**
 * Layout interface.
 * <p>
 * A layout is a group of {@link Widget} instances, built from a
 * {@link LayoutDefinition} in a given mode.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public interface Layout extends Serializable {

    /**
     * Returns the layout id, unique within the facelet context.
     */
    String getId();

    /**
     * Sets the layout id, unique within the facelet context.
     */
    void setId(String id);

    /**
     * Returns the layout name used to identify it within the layout service.
     */
    String getName();

    /**
     * Returns the layout mode.
     */
    String getMode();

    /**
     * Returns the template used to render widgets.
     */
    String getTemplate();

    /**
     * Returns the table of widgets.
     * <p>
     * This list is computed from the {@link LayoutDefinition} rows.
     * <p>
     * Widgets that are not found are ignored.
     */
    LayoutRow[] getRows();

    /**
     * Returns the maximum number of columns.
     */
    int getColumns();

    /**
     * Returns widget with given name.
     * <p>
     * Only widgets of the first level are retrieved.
     *
     * @since 5.2M4
     */
    Widget getWidget(String name);

    /**
     * Returns a widget map, with widget name as key.
     * <p>
     * Only widgets of the first level are retrieved.
     *
     * @since 5.2M4
     */
    Map<String, Widget> getWidgetMap();

    /**
     * Get properties to use in this mode.
     * <p>
     * The way that properties will be mapped to rendered components is managed
     * by the widget type.
     *
     * @since 5.3.1
     */
    Map<String, Serializable> getProperties();

    /**
     * Returns property with given name in this mode.
     *
     * @param name the property name.
     * @return the property value or null if not found.
     * @since 5.3.1
     */
    Serializable getProperty(String name);

    /**
     * Sets property with given name on the layout. If there is already a
     * property with this name on the layout, it will be overridden.
     *
     * @param name the property name.
     * @param value the property value or null if not found.
     * @since 5.3.2
     */
    void setProperty(String name, Serializable value);

}
