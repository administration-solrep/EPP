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
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "publish", help = "Publish a document into a section")
public class Publish implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-override", hasValue = true, help = "If set to false will not override an existing published document with same name. The default is \"true\".")
    protected boolean override = true;

    @Argument(name = "src", index = 0, required = true, completor = DocRefCompletor.class, help = "The document to copy. To use UID references prefix them with 'doc:'.")
    protected String src;

    @Argument(name = "section", index = 1, required = true, completor = DocRefCompletor.class, help = "The target parent. To use UID references prefix them with 'doc:'.")
    protected String dst;

    public void run() {
        DocRef srcRef = ctx.resolveRef(src);
        DocRef dstRef = ctx.resolveRef(dst);
        try {
            ctx.getDocumentService().publish(srcRef, dstRef, override);
        } catch (Exception e) {
            throw new ShellException("Failed to publish document " + srcRef + " to " + dstRef, e);
        }

    }
}
