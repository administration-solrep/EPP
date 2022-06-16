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
import java.io.FileInputStream;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "cat", help = "Print the content of a file")
public class Cat implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "file", index = 0, required = false, help = "The file to print")
    protected File file;

    public void run() {
        ShellConsole console = shell.getConsole();
        if (file == null) {
            file = shell.getContextObject(FileSystem.class).pwd();
        }
        cat(console, file);
    }

    public static void cat(ShellConsole console, File file) {
        if (!file.isFile()) {
            return;
        }
        String content = null;
        try {
            FileInputStream in = new FileInputStream(file);
            try {
                content = FileSystem.readContent(in);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
        console.println(content);
    }
}
