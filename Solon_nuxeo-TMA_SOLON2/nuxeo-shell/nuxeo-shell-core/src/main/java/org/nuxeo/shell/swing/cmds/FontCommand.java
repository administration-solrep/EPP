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
package org.nuxeo.shell.swing.cmds;

import java.awt.Font;

import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.swing.Console;
import org.nuxeo.shell.swing.Theme;
import org.nuxeo.shell.swing.widgets.JFontChooser;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "font", help = "Modify the font used by the shell. This command is available only in UI mode.")
public class FontCommand implements Runnable {

    @Context
    protected Shell shell;

    @Context
    protected Console console;

    public void run() {
        try {
            JFontChooser fc = new JFontChooser();
            fc.setSelectedFont(console.getFont());
            int result = fc.showDialog(console);
            if (result == JFontChooser.OK_OPTION) {
                Font font = fc.getSelectedFont();
                Theme theme = console.getTheme();
                theme.setName("Custom");
                theme.setFont(font);
                shell.setSetting("theme.Custom", theme.toString());
                console.setTheme(theme);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

}
