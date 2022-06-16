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
