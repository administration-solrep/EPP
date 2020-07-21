/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id: DocumentModelInjector.java 29029 2008-01-14 18:38:14Z ldoguin $
 */

package org.nuxeo.ecm.core.io.impl.plugins;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.AbstractDocumentModelWriter;
import org.nuxeo.ecm.core.io.impl.DocumentTranslationMapImpl;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
// TODO: improve it ->
// modify core session to add a batch create method and use it
@SuppressWarnings({"ThrowableInstanceNeverThrown"})
public class DocumentModelInjector extends AbstractDocumentModelWriter {

    private static final Log log = LogFactory.getLog(DocumentModelInjector.class);

   /**
    *
    * @param session the session to the repository where to write
    * @param parentPath where to write the tree. this document will be used as
    *            the parent of all top level documents passed as input. Note
    *            that you may have
    */
   public DocumentModelInjector(CoreSession session, String parentPath) {
       super(session, parentPath);

   }

   public DocumentModelInjector(CoreSession session, String parentPath,
           int saveInterval) {
       super(session, parentPath, saveInterval);
   }

    @Override
    public DocumentTranslationMap write(ExportedDocument xdoc) throws IOException {
        Path path = xdoc.getPath();
        if (path.isEmpty() || path.isRoot()) {
            return null; // TODO avoid to import the root
        }
        path = root.append(path); // compute target path

        try {
            DocumentModel doc = createDocument(xdoc, path);
            DocumentLocation source = xdoc.getSourceLocation();
            DocumentTranslationMap map = new DocumentTranslationMapImpl(
                    source.getServerName(), doc.getRepositoryName());
            map.put(source.getDocRef(), doc.getRef());
            return map;
        } catch (ClientException e) {
            IOException ioe = new IOException(
                    "Failed to import document in repository: "
                            + e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            log.error(e, e);
            return null;
        }
    }

}
