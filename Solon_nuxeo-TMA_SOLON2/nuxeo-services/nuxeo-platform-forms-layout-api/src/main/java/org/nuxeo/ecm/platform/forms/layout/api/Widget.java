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
 * $Id: Widget.java 26808 2007-11-05 12:00:39Z atchertchian $
 */

package org.nuxeo.ecm.platform.forms.layout.api;

import java.io.Serializable;
import java.util.Map;

/**
 * Widget interface.
 * <p>
 * A widget is built from a {@link WidgetDefinition} in a given mode.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public interface Widget extends Serializable {

    /**
     * Returns the widget id, unique within the facelet context.
     */
    String getId();

    /**
     * Sets the widget id, unique within the facelet context.
     */
    void setId(String id);

    /**
     * Returns the widget name used to identify it within a layout.
     */
    String getName();

    /**
     * Returns the layout name.
     */
    String getLayoutName();

    /**
     * Returns the widget type used to render it.
     */
    String getType();

    /**
     * Gets the value name used to compute tag attributes.
     */
    String getValueName();

    /**
     * Sets the value name used to compute tag attributes.
     */
    void setValueName(String valueName);

    /**
     * Returns the list of fields managed by this widget.
     */
    FieldDefinition[] getFieldDefinitions();

    /**
     * Returns the widget mode.
     * <p>
     * This mode can be different from the layout mode.
     */
    String getMode();

    /**
     * Returns the label to use in this mode.
     */
    String getLabel();

    /**
     * Return the help label to use in this mode.
     */
    String getHelpLabel();

    /**
     * Returns true if all labels are messages that need to be translated.
     */
    boolean isTranslated();

    /**
     * Get properties to use in this mode.
     * <p>
     * The way that properties will be mapped to rendered components is managed
     * by the widget type.
     */
    Map<String, Serializable> getProperties();

    /**
     * Returns property with given name in this mode.
     *
     * @param name the property name.
     * @return the property value or null if not found.
     */
    Serializable getProperty(String name);

    /**
     * Sets property with given name on the layout. If there is already a
     * property with this name on the widget, it will be overridden.
     *
     * @param name the property name.
     * @param value the property value or null if not found.
     * @since 5.3.2
     */
    void setProperty(String name, Serializable value);

    /**
     * Returns true if the widget is required.
     * <p>
     * This is a short link for the "required" property, already evaluated from
     * an EL expression (if needed). Defaults to false.
     */
    boolean isRequired();

    /**
     * Returns sub widgets.
     */
    Widget[] getSubWidgets();

    /**
     * Returns the widget level in the widget hierarchy.
     * <p>
     * For instance a standard widget will have a level of 0, and its potential
     * subwidgets will have a level of 1.
     */
    int getLevel();

    /**
     * Returns the select options for this widget.
     *
     * @since 5.4.2
     */
    WidgetSelectOption[] getSelectOptions();

}
