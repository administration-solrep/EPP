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

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "pushd", help = "Change the context document and push the document on the navigation stack.")
public class Pushd implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Argument(name = "doc", index = 0, required = true, completor = DocRefCompletor.class, help = "A reference to the new context document to use. To use UID references prefix them with 'doc:'.")
    protected String ref;

    public void run() {
        ctx.pushDocument(ctx.resolveDocument(ref));
    }

}
