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

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.colorchooser.DefaultColorSelectionModel;

import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.swing.Console;
import org.nuxeo.shell.swing.Theme;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "background", help = "Modify the background color used by the shell. This command is available only in UI mode.")
public class BgColorCommand implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Console console;

    public void run() {
        try {
            DefaultColorSelectionModel model = new DefaultColorSelectionModel(console.getBackground());
            JColorChooser cc = new JColorChooser();
            cc.setSelectionModel(model);
            Color color = JColorChooser.showDialog(console, "Select the background color", console.getBackground());
            if (color != null) {
                Theme theme = console.getTheme();
                theme.setName("Custom");
                theme.setBgColor(color);
                shell.setSetting("theme.Custom", theme.toString());
                console.setTheme(theme);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

}
