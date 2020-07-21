/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id: PagedClassificationsProvider.java 62933 2009-10-14 10:54:33Z ldoguin $
 */

package org.nuxeo.ecm.platform.classification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.DocumentModelIterator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PagedDocumentsProvider;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

/**
 * Paged provider for a document classification.
 *
 * @author Anahide Tchertchian
 */
public class PagedClassificationsProvider implements
        PagedDocumentsProvider {

    private static final long serialVersionUID = 1L;

    protected final List<DocumentModel> documents;

    protected final int pageSize;

    protected int currentPageIndex = 0;

    protected final SortInfo sortInfo;

    protected String name;

    public PagedClassificationsProvider(List<DocumentModel> documents,
            int pageSize, String name, SortInfo sortInfo) {
        this.pageSize = pageSize;
        this.name = name;
        this.sortInfo = sortInfo;
        if (documents == null) {
            documents = new DocumentModelListImpl();
        }
        // set documents sorted
        if (sortInfo != null) {
            Map<String, String> sort = new HashMap<String, String>();
            sort.put(
                    sortInfo.getSortColumn(),
                    sortInfo.getSortAscending() ? DocumentModelComparator.ORDER_ASC
                            : "");
            Collections.sort(documents, new DocumentModelComparator(sort));
        }
        this.documents = documents;
    }

    public DocumentModelList getCurrentPage() {
        return getPage(currentPageIndex);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getCurrentPageOffset() {
        return currentPageIndex * pageSize;
    }

    public int getCurrentPageSize() {
        return getCurrentPage().size();
    }

    public String getCurrentPageStatus() {
        int total = getNumberOfPages();
        int current = currentPageIndex + 1;
        if (total == UNKNOWN_SIZE) {
            return String.format("%d", current);
        } else {
            return String.format("%d/%d", current, total);
        }
    }

    public String getName() {
        return name;
    }

    protected void setCurrentPageIndex(int index) {
        currentPageIndex = index;
    }

    public DocumentModelList getNextPage() {
        setCurrentPageIndex(currentPageIndex + 1);
        return getCurrentPage();
    }

    public int getNumberOfPages() {
        long size = documents.size();
        if (size == DocumentModelIterator.UNKNOWN_SIZE) {
            return UNKNOWN_SIZE;
        }
        return (int) (1 + (size - 1) / pageSize);
    }

    public DocumentModelList getPage(int page) {
        int fromIndex = currentPageIndex * pageSize;
        int toIndex = fromIndex + pageSize;
        int size = documents.size();
        if (toIndex > size) {
            toIndex = size;
        }
        DocumentModelList docsPage = new DocumentModelListImpl();
        docsPage.addAll(documents.subList(fromIndex, toIndex));
        return docsPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getResultsCount() {
        return documents.size();
    }

    public SortInfo getSortInfo() {
        return sortInfo;
    }

    public boolean isNextPageAvailable() {
        return currentPageIndex < getNumberOfPages() - 1;
    }

    public boolean isPreviousPageAvailable() {
        return currentPageIndex > 0;
    }

    public boolean isSortable() {
        return true;
    }

    public void last() {
        int lastPage = getNumberOfPages();
        if (lastPage == UNKNOWN_SIZE) {
            while (isNextPageAvailable()) {
                getNextPage();
            }
        } else {
            setCurrentPageIndex(lastPage - 1);
        }
    }

    public void next() {
        setCurrentPageIndex(currentPageIndex + 1);
    }

    public void previous() {
        if (currentPageIndex > 0) {
            setCurrentPageIndex(currentPageIndex - 1);
        }
    }

    public void refresh() {
        // cannot refresh
    }

    public void rewind() {
        setCurrentPageIndex(0);
    }

    public void setName(String name) {
        this.name = name;
    }

}
