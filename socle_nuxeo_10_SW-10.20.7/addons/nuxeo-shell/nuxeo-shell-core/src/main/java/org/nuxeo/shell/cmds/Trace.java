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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "trace", help = "Print the last error stack trace if any")
public class Trace implements Runnable {

    @Context
    protected Shell shell;

    public void run() {
        Throwable t = (Throwable) shell.getProperty("last.error");
        if (t != null) {
            shell.getConsole().println();
            shell.getConsole().println(getStackTrace(t));
        } else {
            shell.getConsole().println("No stack trace to print");
        }
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        if (t.getCause() != null && t.getCause().getClass().getName().endsWith(".RemoteException")) {
            pw.println("Dumping remote stack trace:");
            t.getCause().printStackTrace(pw);
        }
        pw.close();
        return sw.getBuffer().toString();
    }
}
