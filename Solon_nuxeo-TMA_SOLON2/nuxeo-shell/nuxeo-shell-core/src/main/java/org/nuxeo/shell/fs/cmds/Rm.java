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
package org.nuxeo.shell.fs.cmds;

import java.io.File;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "rm", help = "Remove a file or directory")
public class Rm implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "file", index = 0, required = true, help = "The directory path to create")
    protected File file;

    @Parameter(name = "-r", hasValue = false, help = "Recursive remove dorectories")
    protected boolean recursive;

    public void run() {
        if (file.isDirectory() && !recursive) {
            shell.getConsole().println("Use rm -r to remove directories");
            return;
        }
        FileSystem.deleteTree(file);
    }

}
