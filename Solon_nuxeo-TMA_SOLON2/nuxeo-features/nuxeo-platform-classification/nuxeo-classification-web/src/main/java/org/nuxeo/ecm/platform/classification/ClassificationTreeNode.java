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
 * $Id: ClassificationTreeNode.java 58610 2008-11-04 17:29:03Z atchertchian $
 */

package org.nuxeo.ecm.platform.classification;

import java.util.Collections;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.Sorter;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNodeImpl;

/**
 * Tree node taking care of classified documents within a document
 *
 * @author Anahide Tchertchian
 */
public class ClassificationTreeNode extends DocumentTreeNodeImpl {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ClassificationTreeNode.class);

    public ClassificationTreeNode(DocumentModel document, Filter filter,
            Sorter sorter) {
        super(document, filter, sorter);
    }

    @Override
    public void fetchChildren() {
        try {
            // fetch usual children (sub folders and saved searches)
            children = new LinkedHashMap<Object, DocumentTreeNodeImpl>();
            CoreSession session = CoreInstance.getInstance().getSession(
                    sessionId);

            // get and filter
            DocumentModelList coreChildren = session.getChildren(
                    document.getRef(), null, SecurityConstants.READ, filter,
                    sorter);
            for (DocumentModel child : coreChildren) {
                String identifier = child.getId();
                children.put(identifier, new ClassificationTreeNode(child,
                        filter, sorter));
            }

            // add classified files as children, respecting PLE-252 (folders
            // first) as classified files are never folderish
            DocumentModelList classifChildren = ClassificationHelper.getClassifiedDocuments(
                    document, session);
            // sort according to original sorter
            Collections.sort(classifChildren, sorter);
            for (DocumentModel child : classifChildren) {
                String identifier = child.getId();
                children.put(identifier, new ClassificationTreeNode(child,
                        filter, sorter));
            }
        } catch (ClientException e) {
            log.error(e);
        }
    }

}
