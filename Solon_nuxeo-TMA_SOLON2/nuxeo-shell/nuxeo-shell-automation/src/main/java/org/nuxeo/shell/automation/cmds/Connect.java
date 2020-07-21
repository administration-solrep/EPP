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

import java.util.Map;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.AutomationFeature;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "connect", help = "Connect to a remote automation server")
public class Connect implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "url", index = 0, required = false, help = "The url of the automation server")
    protected String url;

    @Parameter(name = "-u", hasValue = true, help = "The username")
    protected String username;

    @Parameter(name = "-p", hasValue = true, help = "The password")
    protected String password;
    
    @Parameter(name = "-d", hasValue = true, help = "The initial directory")
    protected String initialDirectory;
    
    public void run() {
        Map<String, String> args = (Map<String, String>) shell.getMainArguments();
        if (username == null && args != null) {
            username = args.get("-u");
        }
        if (password == null && args != null) {
            password = args.get("-p");
        }
        if (initialDirectory == null && args != null) {
            initialDirectory = args.get("-d");
        }
        if (initialDirectory == null) {
            initialDirectory = "/";
        }
        if (url == null && args != null) {
            url = args.get("#1");
        }
        if (username != null && password == null) {
            password = shell.getConsole().readLine("Password: ", '*');
        }
        try {
            shell.getFeature(AutomationFeature.class).connect(url, username,
                    password, initialDirectory);
        } catch (ShellException e) {
            throw e;
        } catch(Throwable e) {
            throw new ShellException("Failed to connect to " + url, e);
        }
    }

}
