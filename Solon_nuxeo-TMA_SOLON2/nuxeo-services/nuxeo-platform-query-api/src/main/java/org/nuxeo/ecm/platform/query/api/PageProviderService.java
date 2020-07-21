/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.query.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.SortInfo;

/**
 * @author Anahide Tchertchian
 * @since 5.4
 */
public interface PageProviderService extends Serializable {

    /**
     * Returns a named page provider definition.
     * <p>
     * Useful to share the definition between the page provider service, and
     * the content view service (as content views can reference a named page
     * provider that is already registered instead of redefining it).
     *
     * @since 5.4
     * @param name the page provider name
     * @return the page provider definition or null, if no page provider with
     *         this name was found.
     */
    PageProviderDefinition getPageProviderDefinition(String name);

    /**
     * Returns an instance of page provider with given name and definition.
     * <p>
     * Useful to share the definition between the page provider service, and
     * the content view service (as content views can reference a named page
     * provider that is already registered instead of redefining it).
     *
     * @since 5.4
     * @param name the name that will be set on the provider.
     * @param desc the definition used to build the provider instance.
     * @param sortInfos sort information to set on the provider instance.
     * @param pageSize the provider page size.
     * @param currentPage the provider current page index.
     * @param properties the provider properties
     * @param parameters the provider parameters.
     * @return the page provider instance.
     * @throws ClientException if the page provider instantiation fails.
     */
    PageProvider<?> getPageProvider(String name, PageProviderDefinition desc,
            List<SortInfo> sortInfos, Long pageSize, Long currentPage,
            Map<String, Serializable> properties, Object... parameters)
            throws ClientException;

    /**
     * Returns the page provider computed from the content view with given
     * name. Its properties are resolved using current {@link FacesContext}
     * instance if they are EL Expressions.
     * <p>
     * If not null, parameters sortInfos and pageSize will override information
     * computed in the XML file. If not null, currentPage will override default
     * current page (0).
     *
     * @throws ClientException
     */
    PageProvider<?> getPageProvider(String name, List<SortInfo> sortInfos,
            Long pageSize, Long currentPage,
            Map<String, Serializable> properties, Object... parameters)
            throws ClientException;

}
