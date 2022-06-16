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

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.Interactive;
import org.nuxeo.shell.equinox.Connector;
import org.nuxeo.shell.equinox.EquinoxCommandCompletor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "run", help = "Run an Equinox command")
public class Run implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Connector connector;

    @Argument(name = "cmd", index = 0, completor = EquinoxCommandCompletor.class, help = "The command to run. If not specified the list of all commands is displayed.")
    protected String cmd;

    @Argument(name = "cmd arg1", index = 1, help = "Command argument")
    protected String arg1;

    @Argument(name = "cmd arg2", index = 2, help = "Command argument")
    protected String arg2;

    @Argument(name = "cmd arg3", index = 3, help = "Command argument")
    protected String arg3;

    @Argument(name = "cmd arg4", index = 4, help = "Command argument")
    protected String arg4;

    @Argument(name = "cmd arg5", index = 5, help = "Command argument")
    protected String arg5;

    @Argument(name = "cmd arg6", index = 6, help = "Command argument")
    protected String arg6;

    public void run() {
        if (cmd == null) {
            cmd = "help";
        } else {
            cmd = Interactive.getCurrentCmdLine().trim();
            cmd = cmd.substring("run".length()).trim();
        }
        shell.getConsole().println(connector.send(cmd));
    }
}
