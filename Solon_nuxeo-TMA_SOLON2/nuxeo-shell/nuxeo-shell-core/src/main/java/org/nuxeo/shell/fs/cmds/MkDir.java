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

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "mkdir", help = "Create a directory in local file system")
public class MkDir implements Runnable {

    @Argument(name = "file", index = 0, required = true, help = "The directory path to create")
    protected File file;

    public void run() {
        file.mkdirs();
    }

}
