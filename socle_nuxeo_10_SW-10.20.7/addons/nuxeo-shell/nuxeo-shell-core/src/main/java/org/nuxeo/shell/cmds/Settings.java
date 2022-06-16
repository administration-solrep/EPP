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

import java.io.File;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.fs.cmds.Cat;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "settings", help = "Print or modify the shell settings.")
public class Settings implements Runnable {

    @Context
    protected Shell shell;

    @Argument(name = "name", index = 0, required = false, help = "The variable to print or set.")
    protected String name;

    @Argument(name = "value", index = 1, required = false, help = "The variable value to set.")
    protected String value;

    @Parameter(name = "-reset", hasValue = false, help = "Reset settings to their defaults. Need to restart shell.")
    protected boolean reset;

    public void run() {
        File file = shell.getSettingsFile();
        if (reset) {
            file.delete();
            return;
        }
        try {
            if (name == null) {
                Cat.cat(shell.getConsole(), file);
            } else if (value == null) {
                shell.getConsole().println((String) shell.getSetting(name, "NULL"));
            } else {
                shell.setSetting(name, value);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
