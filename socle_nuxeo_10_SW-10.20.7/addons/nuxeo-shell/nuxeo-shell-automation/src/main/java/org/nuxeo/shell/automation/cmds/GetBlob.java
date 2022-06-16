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

import java.io.File;

import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "getfile", help = "Get a document attached file")
public class GetBlob implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-xpath", hasValue = true, help = "The xpath of the blob property to get. Defaults to the one used by the File document type.")
    protected String xpath;

    @Parameter(name = "-todir", hasValue = true, help = "An optional target directory to save the file. The default is the current working directory.")
    protected File todir;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "The target document. If not specified the current document is used. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        ShellConsole console = ctx.getShell().getConsole();
        DocRef doc = ctx.resolveRef(path);
        try {
            FileBlob blob = ctx.getDocumentService().getBlob(doc, xpath);
            String fname = blob.getFileName();
            if (fname == null || fname.length() == 0) {
                fname = "unnamed_blob";
            }
            if (todir == null) {
                todir = ctx.getShell().getContextObject(FileSystem.class).pwd();
            }
            File dst = new File(todir, fname);
            blob.getFile().renameTo(dst);
            console.println("File saved to: " + dst.getAbsolutePath());
        } catch (Exception e) {
            throw new ShellException("Failed to get file from " + doc, e);
        }

    }
}
