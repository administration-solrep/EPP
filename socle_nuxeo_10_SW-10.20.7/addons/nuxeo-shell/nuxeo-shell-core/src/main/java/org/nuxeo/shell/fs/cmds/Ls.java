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
package org.nuxeo.shell.fs.cmds;

import java.io.File;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "ls", help = "List file names in a local directory")
public class Ls implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "file", index = 0, required = false, help = "A local directory to list its content. Defaults to the working directory")
    protected File file;

    public void run() {
        ShellConsole console = shell.getConsole();
        if (file == null) {
            file = shell.getContextObject(FileSystem.class).pwd();
        }
        String[] names = file.list();
        if (names != null) {
            for (String name : names) {
                console.println(name);
            }
        }
    }

}
