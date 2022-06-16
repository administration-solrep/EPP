/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.shell.automation.cmds;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.DocTypeCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "mkdir", help = "Create a document of the given type")
public class MkDir implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-title", hasValue = true, help = "An optional document title.")
    protected String title;

    @Argument(name = "type", index = 0, required = true, completor = DocTypeCompletor.class, help = "The document type")
    protected String type;

    @Argument(name = "path", index = 1, required = true, completor = DocRefCompletor.class, help = "The document path")
    protected String path;

    public void run() {
        Path p = ctx.resolvePath(path);
        Document document = new Document(p.lastSegment(), type);
        if (title != null) {
            document.set("dc:title", title);
        }
        try {
            ctx.getDocumentService().createDocument(p.getParent().toString(), document);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
