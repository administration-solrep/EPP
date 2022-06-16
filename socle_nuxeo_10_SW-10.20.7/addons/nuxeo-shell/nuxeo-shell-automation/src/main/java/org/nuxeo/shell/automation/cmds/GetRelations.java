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
import org.nuxeo.ecm.automation.client.model.Documents;
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
@Command(name = "getrel", help = "Get realtions between two documents")
public class GetRelations implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-out", hasValue = false, help = "Is the document the relation subject? This flag is by default on true.")
    protected boolean outgoing = true;

    @Parameter(name = "-in", hasValue = false, help = "Is the document the relation object?")
    protected boolean ingoing;

    @Parameter(name = "-predicate", hasValue = true, help = "The relation predicate - requested.")
    protected String predicate;

    @Parameter(name = "-graphName", hasValue = true, help = "The graph name - optional.")
    protected String graphName;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "The document involved in the relation")
    protected String path;

    public void run() {
        if (predicate == null) {
            throw new ShellException("Relation predicate is required!");
        }
        if (ingoing) {
            outgoing = false;
        }
        ShellConsole console = ctx.getShell().getConsole();
        DocRef docRef = ctx.resolveRef(path);
        try {
            Documents docs = (Documents) ctx.getDocumentService().getRelations(docRef, predicate, outgoing, graphName);
            for (Document doc : docs) {
                DocumentHelper.printPath(console, doc);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
