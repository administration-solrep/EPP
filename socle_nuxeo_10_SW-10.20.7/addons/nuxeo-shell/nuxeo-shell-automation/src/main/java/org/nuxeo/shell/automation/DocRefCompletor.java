/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell.automation;

import java.util.Collections;
import java.util.List;

import jline.Completor;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocRefCompletor implements Completor {

    protected RemoteContext ctx;

    public DocRefCompletor() {
        this(Shell.get().getContextObject(RemoteContext.class));
    }

    public DocRefCompletor(RemoteContext ctx) {
        this.ctx = ctx;
    }

    protected Document fetchDocument(String path) {
        try {
            return ctx.resolveDocument(path);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int complete(String buffer, int cursor, List clist) {
        Document cdoc = ctx.getDocument();
        String prefix = "";
        if (buffer != null) {
            if (buffer.endsWith("/")) {
                Path path = new Path(buffer).removeTrailingSeparator();
                prefix = buffer;
                buffer = "";
                cdoc = fetchDocument(path.toString());
            } else if (buffer.indexOf('/') != -1) {
                Path path = new Path(buffer);
                buffer = path.lastSegment();
                prefix = path.getParent().toString();
                cdoc = fetchDocument(prefix);
                prefix += '/';
            }
        }
        if (cdoc == null) {
            return -1;
        }
        try {
            Documents docs = ctx.getDocumentService().getChildren(cdoc);
            for (Document doc : docs) {
                String name = new Path(doc.getPath()).lastSegment();
                if (buffer == null) {
                    clist.add(name);
                } else if (name.startsWith(buffer)) {
                    clist.add(prefix + name);
                }
            }
            Collections.sort(clist);
            if (clist.size() == 1) { // TODO add trailing / only if folderish
                clist.set(0, ((String) clist.get(0)) + '/');
            }
            return clist.isEmpty() ? -1 : 0;
        } catch (Exception e) {
            throw new ShellException("Failed to gather children for " + buffer, e);
        }
    }
}
