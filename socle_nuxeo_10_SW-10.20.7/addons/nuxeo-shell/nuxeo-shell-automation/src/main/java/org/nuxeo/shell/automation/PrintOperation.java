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
package org.nuxeo.shell.automation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "print", help = "Print operation(s) definition")
public class PrintOperation implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-u", hasValue = true, help = "The username if any.")
    protected String u;

    @Parameter(name = "-p", hasValue = true, help = "The password if any.")
    protected String p;

    @Parameter(name = "-out", hasValue = true, help = "An optional file to save the operation definition into. If not used the definition will be printed on stdout.")
    protected File out;

    @Argument(name = "operation", index = 0, required = false, completor = OperationNameCompletor.class, help = "The opertation to print.")
    protected String name;

    @Override
    public void run() {
        try {
            String url = ctx.getClient().getBaseUrl();
            HttpGet get = new HttpGet(url + (name == null ? "" : name));
            if (u != null && p != null) {
                // TODO be able to reuse the context of the automation client
                String base64 = Base64.getEncoder().encodeToString((u + ":" + p).getBytes(UTF_8));
                get.setHeader("Authorization", "Basic " + base64);
            }
            HttpResponse r = ctx.getClient().http().execute(get);
            try (InputStream in = r.getEntity().getContent()) {
                String content = IOUtils.toString(in, UTF_8);
                if (out == null) {
                    ctx.getShell().getConsole().println(content);
                } else {
                    FileUtils.writeStringToFile(out, content, UTF_8);
                }
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

}
