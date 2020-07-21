/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.shell.automation.cmds;

import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "getp", help = "Get the value of a document property")
public class GetProperty implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-xpath", hasValue = true, help = "The xpath of the property to get. This parameter is required.")
    protected String xpath;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "The target document. If not specified the current document is used. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        DocRef doc = ctx.resolveRef(path);
        if (xpath == null) {
            throw new ShellException("-xpath parameter is required!");
        }
        try {
            Object p = ctx.getDocumentService().getDocument(doc, "*").getProperties().get(
                    xpath);
            if (p != null) {
                ctx.getShell().getConsole().println(p.toString());
            }
        } catch (Exception e) {
            throw new ShellException("Failed to get property on " + doc, e);
        }

    }
}
