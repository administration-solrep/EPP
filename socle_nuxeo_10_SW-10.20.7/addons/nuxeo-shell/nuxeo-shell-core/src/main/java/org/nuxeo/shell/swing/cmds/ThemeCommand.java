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
package org.nuxeo.shell.swing.cmds;

import jline.SimpleCompletor;

import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.swing.Console;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "theme", help = "Modify the theme used by the shell. This command is available only in UI mode.")
public class ThemeCommand implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Console console;

    @Argument(name = "name", index = 0, required = false, completor = ThemeCompletor.class, help = "The theme name to set. If not specified the current theme is printed.")
    protected String name;

    public void run() {
        try {
            if (name != null) {
                shell.setSetting("theme", name);
                console.loadDefaultTheme(shell);
            } else {
                shell.getConsole().println(shell.getSetting("theme", "Default"));
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    public static class ThemeCompletor extends SimpleCompletor {
        public ThemeCompletor() {
            super(new String[] { "Default", "Linux", "White", "Custom" });
        }
    }
}
