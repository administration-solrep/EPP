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
package org.nuxeo.shell.cmds;

import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "version", help = "Print Nuxeo Shell Version")
public class Version implements Runnable {

    @Context
    Shell shell;

    public void run() {
        shell.getConsole().println(getVersionMessage());
    }

    public static String getVersionMessage() {
        StringBuilder buf = new StringBuilder();
        String crlf = System.getProperty("line.separator");
        for (String v : Shell.get().getVersions()) {
            buf.append(v).append(crlf);
        }
        return buf.toString();
    }

    public static String getShellVersion() {
        return Shell.class.getPackage().getImplementationVersion();
    }

}
