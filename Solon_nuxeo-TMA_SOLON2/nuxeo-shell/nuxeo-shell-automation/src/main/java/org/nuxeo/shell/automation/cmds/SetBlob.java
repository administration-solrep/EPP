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

import java.io.File;

import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
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
@Command(name = "putfile", help = "Attach a file to a document")
public class SetBlob implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-xpath", hasValue = true, help = "The xpath of the blob property to set. Defaults to the one used by the File document type.")
    protected String xpath;

    @Argument(name = "file", index = 0, required = true, help = "The file to upload")
    protected File file;

    @Argument(name = "doc", index = 1, required = false, completor = DocRefCompletor.class, help = "The target document. If not specified the current document is used. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        DocRef doc = ctx.resolveRef(path);
        try {
            ctx.getDocumentService().setBlob(doc, new FileBlob(file), xpath);
        } catch (Exception e) {
            throw new ShellException("Failed to attach file on " + doc, e);
        }

    }
}
