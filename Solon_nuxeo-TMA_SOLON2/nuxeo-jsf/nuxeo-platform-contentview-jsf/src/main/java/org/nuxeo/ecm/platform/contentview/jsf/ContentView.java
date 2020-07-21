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
package org.nuxeo.ecm.platform.contentview.jsf;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProvider;

/**
 * A content view is a notion to handle lists of objects rendering, as well as
 * query filters to build the list.
 * <p>
 * It has a name that will be the resulting page provider name too. It handles
 * a page provider and accepts configuration needed to handle rendering, like
 * the search layout (for filtering options), the result layout (for results
 * rendering), actions (for buttons available when selecting result objects),
 * the selection list name...
 * <p>
 * It also handles refresh or reset of its provider, depending on its cache key
 * and refresh events configuration.
 *
 * @author Anahide Tchertchian
 * @since 5.4
 */
public interface ContentView extends Serializable {

    public static final String SEARCH_DOCUMENT_EL_VARIABLE = "searchDocument";

    /**
     * Returns the name of this content view
     */
    String getName();

    /**
     * Returns a title for this content view
     */
    String getTitle();

    /**
     * Returns a boolean stating if title has to be translated
     *
     * @see #getTitle()
     */
    boolean getTranslateTitle();

    /**
     * Returns true is the title should be displayed before the result layout
     * selector.
     *
     * @since 5.4.2
     */
    boolean getShowTitle();

    /**
     * Returns the selection list name
     */
    String getSelectionListName();

    /**
     * Returns the pagination type to be used in pagination rendering
     */
    String getPagination();

    /**
     * Returns the list of action categories to display buttons available on
     * selection of items.
     */
    List<String> getActionsCategories();

    /**
     * Returns the list of flags set on this content view, useful to group
     * them, see {@link ContentViewService#getContentViewNames(String)}
     */
    List<String> getFlags();

    /**
     * Returns the search layout, used to filter results.
     */
    ContentViewLayout getSearchLayout();

    /**
     * Returns the result layouts, used to display results.
     */
    List<ContentViewLayout> getResultLayouts();

    /**
     * Returns the current result layout, as set using
     * {@link #setCurrentResultLayout(ContentViewLayout)}, or the first of
     * defined result layouts when not set.
     */
    ContentViewLayout getCurrentResultLayout();

    /**
     * Sets the current result layout.
     */
    void setCurrentResultLayout(ContentViewLayout layout);

    /**
     * Returns the current page size, as set using
     * {@link #setCurrentPageSize(Long)}, or the page size set on current page
     * provider if not null.
     */
    Long getCurrentPageSize();

    /**
     * Sets the current page size.
     */
    void setCurrentPageSize(Long pageSize);

    /**
     * @deprecated use {@link #getCurrentResultLayoutColumns()} instead
     */
    @Deprecated
    List<String> getResultLayoutColumns();

    /**
     * Returns the list of selected result layout columns, resolving the bound
     * EL expression set in the content view definition if columns where not
     * explicitely set with {@link #setCurrentResultLayoutColumns(List)}.
     *
     * @since 5.4.2
     */
    List<String> getCurrentResultLayoutColumns();

    /**
     * Sets the list of result layout columns.
     *
     * @since 5.4.2
     */
    void setCurrentResultLayoutColumns(List<String> resultColumns);

    /**
     * Returns the cache key for this content view provider, resolving from the
     * current {@link FacesContext} instance if it's an EL expression.
     */
    String getCacheKey();

    /**
     * Returns the cache size for this content view.
     */
    Integer getCacheSize();

    /**
     * Returns the icon relative path for this content view.
     */
    String getIconPath();

    /**
     * Returns the query parameters for this content view provider provider,
     * resolving from the current {@link FacesContext} instance if they are EL
     * expressions.
     */
    Object[] getQueryParameters();

    /**
     * Returns the list of event names that should trigger a refresh of this
     * content view page provider.
     */
    List<String> getRefreshEventNames();

    /**
     * Returns the list of event names that should trigger a reset of this
     * content view page provider.
     */
    List<String> getResetEventNames();

    /**
     * Gets page provider according to given parameters
     *
     * @param searchDocument document that will be set on the page provider. If
     *            this document is null, we try to retrieve the content view
     *            document model calling {@link #getSearchDocumentModel()}. If
     *            it is not null, it is set on the page provider.
     * @param sortInfos if not null, will override default sort info put in the
     *            page provider XML description
     * @param pageSize if not null, will override default page size put in the
     *            page provider XML description
     * @param currentPage if not null, will set the current page to given one
     * @param params if not null, will set the parameters on provider. If null,
     *            will take parameters as resolved on the content view from the
     *            XML configuration, see {@link #getQueryParameters()}
     */
    PageProvider<?> getPageProvider(DocumentModel searchDocument,
            List<SortInfo> sortInfos, Long pageSize, Long currentPage,
            Object... params) throws ClientException;

    /**
     * Gets page provider according to given parameters
     *
     * @see #getPageProvider(DocumentModel, List, Long, Long, Object...) using
     *      null as every argument except params
     * @throws ClientException
     */
    PageProvider<?> getPageProviderWithParams(Object... params)
            throws ClientException;

    /**
     * Gets page provider according to given parameters
     *
     * @see #getPageProvider(DocumentModel, List, Long, Long, Object...), using
     *      null as every argument
     * @throws ClientException
     */
    PageProvider<?> getPageProvider() throws ClientException;

    /**
     * Returns the current page provider, or null if methods
     * {@link #getPageProvider()},
     * {@link #getPageProvider(DocumentModel, List, Long, Long, Object...)} or
     * {@link #getPageProviderWithParams(Object...)} were never called before.
     */
    PageProvider<?> getCurrentPageProvider();

    /**
     * Resets the page provider.
     * <p>
     * A new page provider will be computed next time
     * {@link #getPageProviderWithParams(Object...)} is called. Sort
     * information and query parameters will have to be re-generated.
     */
    void resetPageProvider();

    /**
     * Refreshes the current page provider if not null, see
     * {@link PageProvider#refresh()}.
     * <p>
     * Sort information and query parameters and current page are kept.
     */
    void refreshPageProvider();

    /**
     * Refreshes the current page provider if not null, see
     * {@link PageProvider#refresh()}, and resets the current page to the
     * first one.
     * <p>
     * Sort information and query parameters are kept.
     */
    void refreshAndRewindPageProvider();

    /**
     * Returns true is this content view can use the global page size set on
     * the application.
     */
    boolean getUseGlobalPageSize();

    /**
     * Returns true is the page size selector should be displayed.
     *
     * @since 5.4.2
     */
    boolean getShowPageSizeSelector();

    /**
     * Returns true is the refresh page icon should be displayed.
     *
     * @since 5.4.2
     */
    boolean getShowRefreshPage();

    /**
     * Returns the search document model as set on the content view.
     * <p>
     * If this document is null and a EL binding has been set on the content
     * view description, the document model will be resolved from this binding,
     * and set as the search document model.
     */
    DocumentModel getSearchDocumentModel();

    /**
     * Sets the search document model to be passed on the page provider, and
     * set it also on the current page provider if not null.
     */
    void setSearchDocumentModel(DocumentModel doc);

    /**
     * Resets the search document model, setting it to null so that it's
     * recomputed when calling {@link #getSearchDocumentModel()}
     */
    void resetSearchDocumentModel();

    /**
     * Returns the search document model type as defined in the XML
     * configuration.
     */
    String getSearchDocumentModelType();

    /**
     * Returns a sentence to display when this content view is empty
     *
     * @since 5.4.2
     */
    String getEmptySentence();

    /**
     * Returns a boolean stating if sentence to display is empty has to be
     * translated
     *
     * @see #getEmptySentence()
     * @since 5.4.2
     */
    boolean getTranslateEmptySentence();

}
