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
package org.nuxeo.shell.swing.widgets;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.nuxeo.shell.swing.Console;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings("serial")
public class HistoryFinder extends JTextField implements DocumentListener {

    protected Console console;

    public HistoryFinder(Console console) {
        this.console = console;
        getDocument().addDocumentListener(this);
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
            setVisible(false);
            getParent().validate();
            console.requestFocus();
            e.consume();
        } else if (code == KeyEvent.VK_ESCAPE) {
            console.getCmdLine().setText("");
            setVisible(false);
            getParent().validate();
            console.requestFocus();
            e.consume();
        }
    }

    @SuppressWarnings("unchecked")
    public String getMatch() {
        String text = getText();
        List<String> list = console.getHistory().getHistoryList();
        for (int i = list.size() - 1; i >= 0; i--) {
            String entry = list.get(i);
            int k = entry.indexOf(text);
            if (k > -1) {
                return entry;
            }
        }
        return null;
    }

    public void changedUpdate(DocumentEvent e) {
        String text = getMatch();
        if (text != null) {
            console.getCmdLine().setText(text);
        }
    }

    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

}
