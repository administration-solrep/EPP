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
package org.nuxeo.shell.cmds;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "use", help = "Switch the current command namespace. If no namespace is specified the current namepsace name is printed.")
public class Use implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "name", index = 0, required = false, help = "The command namespace to use")
    protected String name;

    public void run() {
        if (name != null) {
            CommandRegistry old = shell.setActiveRegistry(name);
            if (old != null) {
                shell.getConsole().println(old.getName() + " -> " + name);
            }
        } else {
            shell.getConsole().println(shell.getActiveRegistry().getName());
        }
    }
}
