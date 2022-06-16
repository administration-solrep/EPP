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

import jline.ConsoleReader;
import jline.Terminal;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class SwingTerminal extends Terminal {

    protected Console console;

    public SwingTerminal(Console console) {
        this.console = console;
    }

    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public boolean getEcho() {
        return true;
    }

    @Override
    public boolean isANSISupported() {
        return false;
    }

    @Override
    public void initializeTerminal() {
        // nothing we need to do (or can do) for windows.
    }

    @Override
    public boolean isEchoEnabled() {
        return true;
    }

    @Override
    public void enableEcho() {
    }

    @Override
    public void disableEcho() {
    }

    /**
     * Always returng 80, since we can't access this info on Windows.
     */
    @Override
    public int getTerminalWidth() {
        return 80;
    }

    /**
     * Always returng 24, since we can't access this info on Windows.
     */
    @Override
    public int getTerminalHeight() {
        return 80;
    }

    @Override
    public void beforeReadLine(ConsoleReader reader, String prompt, Character mask) {
        if (mask != null) {
            console.setMask(mask);
        }
    }

    @Override
    public void afterReadLine(ConsoleReader reader, String prompt, Character mask) {
        console.setMask(null);
    }
}
