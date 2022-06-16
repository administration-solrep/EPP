/*
 * (C) Copyright 2006-2014 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.shell.automation.cmds;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jline.ANSIBuffer;

import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.automation.Scripting;
import org.nuxeo.shell.utils.ANSICodes;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "script", help = "Run a script on the server")
public class Script implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-ctx", hasValue = true, help = "Use this to set execution context variables. Syntax is: \"k1=v1,k1=v2\"")
    protected String ctxVars;

    @Parameter(name = "-s", hasValue = true, help = "Use this to change the separator used in context variables. THe default is ','")
    protected String sep = ",";

    @Argument(name = "file", index = 0, required = true, help = "The script file. Must have a .mvel or .groovy extension")
    protected File file;

    @Argument(name = "timeout", index = 0, required = false, help = "Transaction timeout in seconds.")
    protected Integer timeout;

    @Override
    public void run() {
        ShellConsole console = ctx.getShell().getConsole();
        FileBlob blob = new FileBlob(file);
        Map<String, Object> args = new HashMap<>();
        if (ctxVars != null) {
            for (String pair : ctxVars.split(sep)) {
                String[] ar = StringUtils.split(pair, '=', true);
                args.put(ar[0], ar[1]);
            }
        }
        try {
            String scriptOutput = Scripting.runScript(ctx, blob, args, timeout);
            if (scriptOutput != null) {
                ANSIBuffer buf = Shell.get().newANSIBuffer();
                ANSICodes.appendTemplate(buf, scriptOutput, false);
                console.println(buf.toString());
            }
        } catch (Exception e) {
            throw new ShellException("Failed to run script", e);
        }
    }

}
