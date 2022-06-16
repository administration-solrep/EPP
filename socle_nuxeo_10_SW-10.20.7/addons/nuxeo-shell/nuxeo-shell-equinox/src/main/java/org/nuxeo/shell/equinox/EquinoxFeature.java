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
package org.nuxeo.shell.equinox;

import java.util.Map;

import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellFeature;
import org.nuxeo.shell.cmds.GlobalCommands;
import org.nuxeo.shell.equinox.cmds.Bundle;
import org.nuxeo.shell.equinox.cmds.Bundles;
import org.nuxeo.shell.equinox.cmds.Connect;
import org.nuxeo.shell.equinox.cmds.Diag;
import org.nuxeo.shell.equinox.cmds.Disconnect;
import org.nuxeo.shell.equinox.cmds.Headers;
import org.nuxeo.shell.equinox.cmds.Packages;
import org.nuxeo.shell.equinox.cmds.Run;
import org.nuxeo.shell.equinox.cmds.Ss;
import org.nuxeo.shell.equinox.cmds.Status;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class EquinoxFeature extends CommandRegistry implements ShellFeature {

    protected Connector connector;

    public EquinoxFeature() {
        super(GlobalCommands.INSTANCE, "equinox");
    }

    @Override
    public void install(Shell shell) {
        shell.addRegistry(this);
        addAnnotatedCommand(Connect.class);
    }

    public void loadCommands() {
        addAnnotatedCommand(Disconnect.class);
        addAnnotatedCommand(Run.class);
        addAnnotatedCommand(Ss.class);
        addAnnotatedCommand(Status.class);
        addAnnotatedCommand(Bundle.class);
        addAnnotatedCommand(Bundles.class);
        addAnnotatedCommand(Packages.class);
        addAnnotatedCommand(Headers.class);
        addAnnotatedCommand(Diag.class);
    }

    @Override
    public String getTitle() {
        return "Equinox Commands";
    }

    @Override
    public String getDescription() {
        return "Provides Equinox console commands for a remote OSGi platform";
    }

    @Override
    public String getPrompt(Shell shell) {
        return "osgi> ";
    }

    public void connect(String url, String username, String password) {
        if (connector != null) {
            return;
        }
        connector = Connector.newConnector(url);
        Shell.get().putContextObject(Connector.class, connector);
        loadCommands();
    }

    public void disconnect() {
        if (connector == null) {
            return;
        }
        connector.disconnect();
        connector = null;
        Shell.get().removeContextObject(Connector.class);
        clear();
        addAnnotatedCommand(Connect.class);
    }

    @Override
    public void autorun(Shell shell) {
        Map<String, String> args = shell.getMainArguments();
        if (args != null) {
            String url = args.get("#1");
            if (url != null) {
                shell.getConsole().println("Connecting to " + url + " ...");
                connect(url, null, null);
            }
        }
        super.autorun(shell);
    }

}
