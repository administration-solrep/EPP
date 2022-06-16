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

import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.Interactive;
import org.nuxeo.shell.cmds.InteractiveShellHandler;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings("serial")
public class ShellApplet extends JApplet implements InteractiveShellHandler {

    protected ConsolePanel panel;

    protected String[] getShellArgs() {
        String host = getParameter("host");
        String user = getParameter("user");
        ArrayList<String> args = new ArrayList<String>();
        if (user != null) {
            args.add("-u");
            args.add(user);
        }
        if (host != null) {
            args.add(host);
        }
        return args.toArray(new String[args.size()]);
    }

    @Override
    public void init() {
        try {
            Shell.get(); // initialize the shell to get default settings
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        panel = new ConsolePanel();
                        add(panel);
                        Interactive.setConsoleReaderFactory(panel.getConsole());
                        Interactive.setHandler(ShellApplet.this);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to start applet", e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final Shell shell = Shell.get();
                    shell.main(getShellArgs());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        panel.getConsole().exit(1);
    }

    public void enterInteractiveMode() {
        Interactive.reset();
        requestFocus(); // doesn't work :/
    }

    public boolean exitInteractiveMode(int code) {
        if (code == 1) {
            // applet stop
            Interactive.reset();
            Shell.reset();
            panel.setVisible(false);
            return true;
        } else {
            // reset console
            panel.getConsole().reset();
            return false;
        }
    }
}
