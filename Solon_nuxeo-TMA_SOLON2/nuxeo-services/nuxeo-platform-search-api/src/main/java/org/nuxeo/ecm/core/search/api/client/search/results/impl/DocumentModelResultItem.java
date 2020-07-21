/*
 * (C) Copyright 2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.search.api.client.search.results.impl;

import java.io.Serializable;
import java.util.HashMap;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.search.api.client.search.results.ResultItem;

/*
 * @author Florent Guillaume
 */
public class DocumentModelResultItem extends HashMap<String, Serializable>
        implements ResultItem {

    private static final long serialVersionUID = 1L;

    public final DocumentModel doc;

    public DocumentModelResultItem(DocumentModel doc) {
        this.doc = doc;
    }

    public String getName() {
        return doc.getName();
    }

    public DocumentModel getDocumentModel() {
        return doc;
    }

}
