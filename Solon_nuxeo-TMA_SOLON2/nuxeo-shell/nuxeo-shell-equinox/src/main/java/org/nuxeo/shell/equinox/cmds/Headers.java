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
package org.nuxeo.shell.equinox.cmds;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.equinox.BundleNameCompletor;
import org.nuxeo.shell.equinox.Connector;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "headers", help = "Print bundle headers")
public class Headers implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Connector connector;

    @Argument(name = "name", index = 0, completor = BundleNameCompletor.class, help = "bundle name or uid")
    protected String name;

    public void run() {
        String cmd = "headers";
        if (name != null) {
            cmd += " " + name;
        }
        shell.getConsole().println(connector.send(cmd));
    }
}
