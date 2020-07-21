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

import org.nuxeo.shell.CommandRegistry;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class GlobalCommands extends CommandRegistry {

    public static final GlobalCommands INSTANCE = new GlobalCommands();

    public GlobalCommands() {
        super(null, "global");
        addAnnotatedCommand(Interactive.class);
        addAnnotatedCommand(Help.class);
        addAnnotatedCommand(Commands.class);
        addAnnotatedCommand(Exit.class);
        addAnnotatedCommand(Use.class);
        addAnnotatedCommand(Trace.class);
        addAnnotatedCommand(Version.class);
        addAnnotatedCommand(Settings.class);
        addAnnotatedCommand(Install.class);
    }

    @Override
    public String getTitle() {
        return "Built-in Commands";
    }

    @Override
    public String getDescription() {
        return "Basic commands provided by the shell";
    }

}
