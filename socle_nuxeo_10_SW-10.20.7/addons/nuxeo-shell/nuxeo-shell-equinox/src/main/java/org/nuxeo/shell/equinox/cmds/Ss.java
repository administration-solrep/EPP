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
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.equinox.BundleNameCompletor;
import org.nuxeo.shell.equinox.Connector;
import org.nuxeo.shell.equinox.StateCompletor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "ss", help = "Display installed bundles (short status)")
public class Ss implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Connector connector;

    @Parameter(name = "-s", hasValue = true, completor = StateCompletor.class, help = "comma separated list of bundle states")
    protected String status;

    @Argument(name = "name", index = 0, completor = BundleNameCompletor.class, help = "segment of bundle symbolic name. If not specified all bundles are listed")
    protected String name;

    public void run() {
        String cmd = "ss";
        if (status != null) {
            cmd += " -s " + status;
        }
        if (name != null) {
            cmd += " " + name;
        }
        shell.getConsole().println(connector.send(cmd));
    }
}
