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

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "install", help = "Install a SH script to launch the shell in the terminal. Available only for UNIX systems.")
public class Install implements Runnable {

    @Context
    Shell shell;

    @Argument(name = "file", required = false, index = 0, help = "the file where to install the shell script. If not used the script will be printed on stdout.")
    protected File file;

    public void run() {
        try {
            if (file == null) {
                // dump on stdout
                shell.getConsole().println(getShellScript());
            } else {
                FileSystem.writeFile(file, getShellScript());
                file.setExecutable(true);
            }
        } catch (Exception e) {
            throw new ShellException("Failed to install shell script", e);
        }
    }

    public String getShellScript() throws Exception {
        InputStream in = Install.class.getClassLoader().getResourceAsStream("run.sh");
        String content = FileSystem.read(in);
        URL url = Install.class.getProtectionDomain().getCodeSource().getLocation();
        String path = new File(url.toURI()).getAbsolutePath();
        return content.replace("%SHELL_JAR%", path);
    }
}
