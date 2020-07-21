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
package org.nuxeo.shell.impl;

import java.io.File;

import jline.Completor;

import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.CommandType;
import org.nuxeo.shell.CompletorProvider;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.Use;
import org.nuxeo.shell.cmds.completors.CommandCompletor;
import org.nuxeo.shell.cmds.completors.CommandRegistryCompletor;
import org.nuxeo.shell.fs.FileCompletor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DefaultCompletorProvider implements CompletorProvider {

    public Completor getCompletor(Shell shell, CommandType cmd, Class<?> type) {
        if (CommandType.class.isAssignableFrom(type)) {
            return new CommandCompletor(shell);
        } else if (File.class.isAssignableFrom(type)) {
            return new FileCompletor();
        } else if (CommandRegistry.class.isAssignableFrom(type)) {
            return new CommandRegistryCompletor(shell);
        } else if (cmd.getCommandClass() == Use.class) {
            return new CommandRegistryCompletor(shell);
        }
        return null;
    }

}
