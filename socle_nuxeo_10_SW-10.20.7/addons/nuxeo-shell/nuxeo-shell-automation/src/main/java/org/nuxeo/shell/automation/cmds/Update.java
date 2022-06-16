/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
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

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "update", help = "Update document properties")
public class Update implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-s", hasValue = true, help = "Use this to change the separator used in properties. The default is ','")
    protected String sep = ",";

    @Argument(name = "properties", index = 0, help = "The propertis to update.")
    protected String props;

    @Argument(name = "path", index = 1, required = false, completor = DocRefCompletor.class, help = "The document path")
    protected String path;

    public void run() {
        Document doc = ctx.resolveDocument(path);
        try {
            for (String pair : props.split(sep)) {
                String[] ar = StringUtils.split(pair, '=', true);
                doc.set(ar[0], ar[1]);
            }
            ctx.getDocumentService().update(doc);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
