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
package org.nuxeo.shell.automation.cmds;

import java.text.SimpleDateFormat;
import java.util.TreeSet;

import jline.ANSIBuffer;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertiesHelper;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "cat", help = "Print document details")
public class Cat implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-schemas", hasValue = true, help = "A filter of schemas to include in the document. Use * for all schemas.")
    protected String schemas;

    @Parameter(name = "-all", hasValue = false, help = "Include all schemas. The -schemas attribute will be ignored if used in conjunction with this one.")
    protected boolean all;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "The document to print. To use UIDs as refences you should prefix them with 'doc:'")
    protected String path;

    public void run() {
        ShellConsole console = ctx.getShell().getConsole();
        if (all) {
            schemas = "*";
        }
        Document doc = ctx.resolveDocument(path, schemas);
        print(console, doc);
    }

    public static void print(ShellConsole console, Document doc) {
        ANSIBuffer buf = Shell.get().newANSIBuffer();
        buf.append(ShellConsole.CRLF);
        buf.bold(doc.getType()).append(" -- ").append(doc.getTitle());
        buf.append(ShellConsole.CRLF);
        buf.append("\tUID: ").append(doc.getId());
        buf.append(ShellConsole.CRLF);
        buf.append("\tPath: ").append(doc.getPath());
        buf.append(ShellConsole.CRLF);
        buf.append("\tType: ").append(doc.getType());
        buf.append(ShellConsole.CRLF);

        if (doc.getLastModified() != null) {
            buf.append("\tLast Modified: ").append(new SimpleDateFormat().format(doc.getLastModified()));
            buf.append(ShellConsole.CRLF);
        }
        buf.append("\tState: ").append(doc.getState() == null ? "none" : doc.getState());
        buf.append(ShellConsole.CRLF);
        buf.append("\tLock: ").append(doc.getLock() == null ? "none" : doc.getLock());
        buf.append(ShellConsole.CRLF);
        buf.append(ShellConsole.CRLF);

        String desc = doc.getString("dc:description");
        if (desc != null && desc.length() > 0) {
            buf.bold("DESCRIPTION");
            buf.append(ShellConsole.CRLF);
            for (String line : StringUtils.split(desc, '\n', true)) {
                buf.append("\t").append(line).append(ShellConsole.CRLF);
            }
            buf.append(ShellConsole.CRLF);
        }
        PropertyMap props = doc.getProperties();
        if (props != null && !props.isEmpty()) {
            TreeSet<String> keys = new TreeSet<String>(props.getKeys());
            buf.bold("PROPERTIES");
            buf.append(ShellConsole.CRLF);
            for (String key : keys) {
                if ("dc:description".equals(key)) {
                    continue;
                }
                Object obj = props.get(key);
                buf.append("\t").append(key).append(" = ");
                if (obj != null) {
                    if (PropertiesHelper.isScalar(obj)) {
                        buf.append(props.getString(key));
                    } else {
                        buf.append(obj.toString());
                    }
                }
                buf.append(ShellConsole.CRLF);
            }
            buf.append(ShellConsole.CRLF);
        }

        console.println(buf.toString());

    }
}
