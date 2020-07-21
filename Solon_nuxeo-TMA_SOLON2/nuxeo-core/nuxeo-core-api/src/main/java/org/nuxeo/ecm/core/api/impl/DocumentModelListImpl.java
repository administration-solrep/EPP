/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bogdan Stefanescu
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * @author Bogdan Stefanescu
 * @author Florent Guillaume
 */
public class DocumentModelListImpl extends ArrayList<DocumentModel> implements
        DocumentModelList {

    private static final long serialVersionUID = 4214422534444037559L;

    protected long totalSize = -1;

    public DocumentModelListImpl() {
    }

    public DocumentModelListImpl(int size) {
        super(size);
    }

    public DocumentModelListImpl(List<DocumentModel> list) {
        super(list);
    }

    /**
     * Constructs a DocumentModelListImpl and sets the "total size" information.
     * <p>
     * The total size is additional information that can be provided in some
     * cases where the list returned is a slice of a bigger list, this is used
     * when getting paged results from a database for instance.
     *
     * @param list the list of documents
     * @param totalSize the total size, with -1 meaning
     *            "same as the list's size"
     */
    public DocumentModelListImpl(List<DocumentModel> list, long totalSize) {
        super(list);
        this.totalSize = totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public long totalSize() {
        if (totalSize == -1) {
            return size();
        }
        return totalSize;
    }

}
