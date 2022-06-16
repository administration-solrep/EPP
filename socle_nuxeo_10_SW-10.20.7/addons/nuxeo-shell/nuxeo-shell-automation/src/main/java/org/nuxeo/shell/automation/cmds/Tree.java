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

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.DocumentHelper;
import org.nuxeo.shell.automation.RemoteContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "tree", help = "List a subtree")
public class Tree implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "A document to list its subtree. If not specified list the current document subtree. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        ShellConsole console = ctx.getShell().getConsole();
        Document root = ctx.resolveDocument(path);
        printTree(console, root, "");
    }

    protected void printTree(ShellConsole console, Document root, String prefix) {
        try {
            DocumentHelper.printName(console, root, prefix.length() == 0 ? prefix : prefix + "+- ");
            prefix += "|  ";
            for (Document doc : ctx.getDocumentService().getChildren(root)) {
                printTree(console, doc, prefix);
            }
        } catch (Exception e) {
            throw new ShellException("Failed to list document " + path, e);
        }
    }
}
