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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.nuxeo.shell.swing.widgets.HistoryFinder;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings("serial")
public class ConsolePanel extends JPanel {

    protected Console console;

    protected HistoryFinder finder;

    public ConsolePanel() throws Exception {
        setLayout(new BorderLayout());
        console = new Console();
        finder = new HistoryFinder(console);
        finder.setVisible(false);
        console.setFinder(finder);
        add(new JScrollPane(console), BorderLayout.CENTER);
        add(finder, BorderLayout.SOUTH);
    }

    public Console getConsole() {
        return console;
    }

    public HistoryFinder getFinder() {
        return finder;
    }

}
