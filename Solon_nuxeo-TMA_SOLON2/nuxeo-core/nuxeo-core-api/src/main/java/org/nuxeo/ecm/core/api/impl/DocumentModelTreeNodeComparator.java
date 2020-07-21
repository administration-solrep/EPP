/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bdelbosc@nuxeo.com
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.impl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import org.nuxeo.common.utils.Path;

/**
 * Comparator to sort a document tree using titles.
 *
 * @author bdelbosc
 */
public class DocumentModelTreeNodeComparator implements
        Comparator<DocumentModelTreeNodeImpl>, Serializable {

    private static final long serialVersionUID = 1L;

    protected final Map<String, String> titles;

    /**
     * Expecting a path/title mapping.
     *
     * @param titles
     */
    public DocumentModelTreeNodeComparator(Map<String, String> titles) {
        this.titles = titles;
    }

    protected String getTitlePath(DocumentModelTreeNodeImpl node) {
        Path path = node.getDocument().getPath();
        String titlePath = "/";

        for (int i = 1; i <= path.segmentCount(); i++) {
            String parentPath = path.uptoSegment(i).toString();
            if (titles.containsKey(parentPath)) {
                titlePath += titles.get(parentPath) + "/";
            } else {
                titlePath += path.segment(i - 1) + "/";
            }
        }
        return titlePath;
    }

    @Override
    public int compare(DocumentModelTreeNodeImpl node1,
            DocumentModelTreeNodeImpl node2) {
        String titlePath1 = getTitlePath(node1);
        String titlePath2 = getTitlePath(node2);
        return titlePath1.compareTo(titlePath2);
    }

}
