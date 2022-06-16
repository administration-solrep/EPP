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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jline.ANSIBuffer;

import org.nuxeo.shell.Command;
import org.nuxeo.shell.CommandType;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "commands", aliases = "cmds", help = "Print a list of available commands")
public class Commands implements Runnable {

    @Context
    protected Shell shell;

    public void run() {
        ShellConsole console = shell.getConsole();
        int termWidth = 0;
        if (console instanceof Interactive) {
            termWidth = ((Interactive) console).getConsole().getTermwidth();
        }
        ANSIBuffer buf = shell.newANSIBuffer();
        Map<String, Set<CommandType>> cmds = shell.getActiveRegistry().getCommandTypesByNamespace();
        for (Map.Entry<String, Set<CommandType>> entry : cmds.entrySet()) {
            buf.bold(entry.getKey());
            buf.append(ShellConsole.CRLF);
            int i = 0;
            for (CommandType type : entry.getValue()) {
                int len = type.getName().length();
                if (len > i) {
                    i = len;
                }
            }
            for (CommandType type : entry.getValue()) {
                int len = i - type.getName().length();
                StringBuilder sb = new StringBuilder();
                sb.append("    ").append(type.getName());
                for (int k = 0; k < len; k++) {
                    sb.append(" ");
                }
                sb.append("    ");
                String prefix = sb.toString();
                buf.append(prefix);
                prefix = makePrefix(sb.length());
                wrap(buf, type.getHelp(), prefix, termWidth);
            }
            buf.append(ShellConsole.CRLF);
        }
        console.println(buf.toString());
    }

    public void wrap(ANSIBuffer buf, String text, String prefix, int termWidth) {
        if (text == null) {
            buf.append(ShellConsole.CRLF);
        } else if (termWidth == 0) {
            buf.append(text);
            buf.append(ShellConsole.CRLF);
        } else {
            List<String> lines = split(text, termWidth - prefix.length());
            if (lines.isEmpty()) {
                buf.append(ShellConsole.CRLF);
                return;
            }
            buf.append(lines.get(0));
            buf.append(ShellConsole.CRLF);
            for (int i = 1, len = lines.size(); i < len; i++) {
                buf.append(prefix);
                buf.append(lines.get(i));
                buf.append(ShellConsole.CRLF);
            }
        }
    }

    protected List<String> split(String text, int width) {
        ArrayList<String> lines = new ArrayList<String>();
        int len = text.length();
        if (len <= width) {
            lines.add(text);
        } else {
            String r = text;
            while (r.length() > width) {
                int p = r.lastIndexOf(' ', width);
                if (p == -1) {
                    lines.add(r.substring(0, width));
                    r = r.substring(width);
                } else {
                    lines.add(r.substring(0, p));
                    r = r.substring(p + 1);
                }
            }
            if (r.length() > 0) {
                lines.add(r);
            }
        }
        return lines;
    }

    protected String makePrefix(int len) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            buf.append(' ');
        }
        return buf.toString();
    }
}
