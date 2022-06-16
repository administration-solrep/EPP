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
package org.nuxeo.shell.fs.cmds;

import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.GlobalCommands;
import org.nuxeo.shell.fs.FileSystem;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class FileSystemCommands extends CommandRegistry {

    public static final FileSystemCommands INSTANCE = new FileSystemCommands();

    public FileSystemCommands() {
        super(GlobalCommands.INSTANCE, "local");
        addAnnotatedCommand(Ls.class);
        addAnnotatedCommand(Pwd.class);
        addAnnotatedCommand(Pushd.class);
        addAnnotatedCommand(Popd.class);
        addAnnotatedCommand(Cd.class);
        addAnnotatedCommand(MkDir.class);
        addAnnotatedCommand(Touch.class);
        addAnnotatedCommand(Rm.class);
        addAnnotatedCommand(Cp.class);
        addAnnotatedCommand(Mv.class);
        addAnnotatedCommand(Cat.class);
    }

    @Override
    public String getTitle() {
        return "File System Commands";
    }

    @Override
    public String getDescription() {
        return "Commands available on the local file system";
    }

    @Override
    public String getPrompt(Shell shell) {
        return System.getProperty("user.name") + ":" + shell.getContextObject(FileSystem.class).pwd().getName() + "$ ";
    }

}
