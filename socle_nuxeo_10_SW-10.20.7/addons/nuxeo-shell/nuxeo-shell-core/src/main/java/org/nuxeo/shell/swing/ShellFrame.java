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
package org.nuxeo.shell.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.Interactive;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings("serial")
public class ShellFrame extends JFrame {

    protected Console console;

    public ShellFrame() throws Exception {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nuxeo Shell");
        JPanel content = (JPanel) getContentPane();
        ConsolePanel panel = new ConsolePanel();
        console = panel.getConsole();
        // Set the window's bounds, centering the window
        content.add(panel, BorderLayout.CENTER);
        // content.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setResizable(true);
    }

    public static void main(String[] args) throws Exception {
        Shell shell = Shell.get();
        ShellFrame term = new ShellFrame();
        term.pack();
        term.setSize(800, 600);
        // term.setExtendedState(Frame.MAXIMIZED_BOTH);
        term.setLocationRelativeTo(null);
        term.setVisible(true);
        term.console.requestFocus();
        Interactive.setConsoleReaderFactory(term.console);
        shell.main(args);
    }

}
