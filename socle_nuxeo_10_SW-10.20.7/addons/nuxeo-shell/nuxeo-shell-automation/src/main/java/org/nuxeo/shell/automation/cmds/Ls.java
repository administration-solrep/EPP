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
package org.nuxeo.shell.automation.cmds;

import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.DocumentHelper;
import org.nuxeo.shell.automation.RemoteContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "ls", help = "List children documents")
public class Ls implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "A document to list its content. If not specified list the current document content. To use UID references prefix them with 'doc:'.")
    protected DocRef root;

    @Parameter(name = "-uid", hasValue = false, help = "If used the documents will be printed using the document UID.")
    protected boolean uid = false;

    @Parameter(name = "-title", hasValue = false, help = "If used the documents will be printed using their dublincore title.")
    protected boolean title = false;

    public void run() {
        ShellConsole console = ctx.getShell().getConsole();
        if (root == null) {
            // get the current document if target doc was not specified.
            root = ctx.getDocument();
        }
        try {
            for (Document doc : ctx.getDocumentService().getChildren(root)) {
                if (uid) {
                    console.println(doc.getId());
                } else if (title) {
                    console.println(doc.getTitle());
                } else {
                    DocumentHelper.printName(console, doc);
                }
            }
        } catch (Exception e) {
            throw new ShellException("Failed to list document " + root, e);
        }

    }
}
