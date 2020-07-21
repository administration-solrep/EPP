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
 *
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
