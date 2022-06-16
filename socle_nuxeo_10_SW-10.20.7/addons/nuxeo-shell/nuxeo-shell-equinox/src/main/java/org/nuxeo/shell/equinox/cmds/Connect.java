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
package org.nuxeo.shell.equinox.cmds;

import java.util.Map;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.equinox.EquinoxFeature;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "connect", help = "Connect to a remote osgi platform")
public class Connect implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "url", index = 0, required = false, help = "The url of the automation server")
    protected String url;

    @Parameter(name = "-u", hasValue = true, help = "The username")
    protected String username;

    @Parameter(name = "-p", hasValue = true, help = "The password")
    protected String password;

    public void run() {
        Map<String, String> args = (Map<String, String>) shell.getMainArguments();
        if (username == null && args != null) {
            username = args.get("-u");
        }
        if (password == null && args != null) {
            password = args.get("-p");
        }
        if (url == null && args != null) {
            url = args.get("#1");
        }
        if (username != null && password == null) {
            password = shell.getConsole().readLine("Password: ", '*');
        }
        try {
            shell.getFeature(EquinoxFeature.class).connect(url, username, password);
        } catch (Exception e) {
            throw new ShellException("Failed to connect to " + url, e);
        }
    }

}
