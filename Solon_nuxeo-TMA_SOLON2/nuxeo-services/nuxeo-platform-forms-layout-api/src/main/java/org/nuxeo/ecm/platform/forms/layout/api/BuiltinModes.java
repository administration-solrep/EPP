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
 * $Id: BuiltinModes.java 28460 2008-01-03 15:34:05Z sfermigier $
 */

package org.nuxeo.ecm.platform.forms.layout.api;

/**
 * List of built-in modes.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class BuiltinModes {

    public static final String ANY = "any";

    public static final String VIEW = "view";

    public static final String EDIT = "edit";

    public static final String BULK_EDIT = "bulkEdit";

    public static final String CREATE = "create";

    public static final String SEARCH = "search";

    /**
     * @deprecated: use {@link #VIEW} instead
     */
    @Deprecated
    public static final String LISTING = "listing";

    public static final String SUMMARY = "summary";

    /**
     * @deprecated: use {@link #VIEW} instead
     * @since 5.4.2
     */
    @Deprecated
    protected static final String HEADER = "header";

    /**
     * @since 5.4.2
     */
    public static final String CSV = "csv";

    /**
     * @since 5.4.2
     */
    public static final String PDF = "pdf";

    /**
     * @since 5.4.2
     */
    public static final String PLAIN = "plain";

    private BuiltinModes() {
    }

    /**
     * Returns true if given layout mode is mapped by default to the edit
     * widget mode.
     */
    public static final boolean isBoundToEditMode(String layoutMode) {
        if (layoutMode != null) {
            if (layoutMode.startsWith(CREATE) || layoutMode.startsWith(EDIT)
                    || layoutMode.startsWith(SEARCH)
                    || layoutMode.startsWith(BULK_EDIT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the default mode to use for a widget, given the layout mode.
     * <p>
     * Returns {@link BuiltinWidgetModes#EDIT} for all modes bound to edit,
     * {@link BuiltinWidgetModes#VIEW} for modes {@link #VIEW},
     * {@link #HEADER} and {@link #SUMMARY}. {@link #PDF} and {@link #CSV} are
     * respectively bound to {@link BuiltinWidgetModes#PDF} and
     * {@link BuiltinWidgetModes#CSV}. In other cases, returns
     * {@link BuiltinWidgetModes#PLAIN}.
     * <p>
     * This method is not called when mode is explicitely set on the widget.
     */
    public static final String getWidgetModeFromLayoutMode(String layoutMode) {
        if (layoutMode != null) {
            if (BuiltinModes.isBoundToEditMode(layoutMode)) {
                return BuiltinWidgetModes.EDIT;
            } else if (layoutMode.startsWith(VIEW)
                    || layoutMode.startsWith(SUMMARY)
                    || layoutMode.startsWith(LISTING)
                    || layoutMode.startsWith(HEADER)) {
                return BuiltinWidgetModes.VIEW;
            } else if (layoutMode.startsWith(CSV)) {
                return BuiltinWidgetModes.CSV;
            } else if (layoutMode.startsWith(PDF)) {
                return BuiltinWidgetModes.PDF;
            }
        }
        return BuiltinWidgetModes.PLAIN;
    }

}