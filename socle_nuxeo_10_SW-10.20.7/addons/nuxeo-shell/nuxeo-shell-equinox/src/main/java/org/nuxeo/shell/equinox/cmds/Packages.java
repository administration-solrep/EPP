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
import org.nuxeo.shell.equinox.Connector;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "packages", help = "Display imported/exported package details")
public class Packages implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Connector connector;

    @Argument(name = "bundle", index = 0, required = false, help = "bundle name or uid")
    protected String name;

    @Argument(name = "pkgname", index = 1, required = false, help = "package name")
    protected String pckname;

    public void run() {
        String cmd = "packages";
        if (name != null) {
            cmd += " " + name;
        }
        if (pckname != null) {
            cmd += " " + pckname;
        }
        shell.getConsole().println(connector.send(cmd));
    }
}
