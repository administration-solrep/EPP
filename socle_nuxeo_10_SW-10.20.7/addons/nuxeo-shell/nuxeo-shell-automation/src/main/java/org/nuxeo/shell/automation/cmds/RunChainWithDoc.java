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

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.ChainCompletor;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "run", help = "Run a server automation chain that accepts a document or void input")
public class RunChainWithDoc implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-void", hasValue = false, help = "Use this to avoid having the server sending back the result.")
    protected boolean isVoid;

    @Parameter(name = "-ctx", hasValue = true, help = "Use this to set execution context variables. Syntax is: k1=v1,k1=v2")
    protected String ctxVars;

    @Parameter(name = "-s", hasValue = true, help = "Use this to change the separator used in context variables. THe default is ','")
    protected String sep = ",";

    @Argument(name = "chain", index = 0, required = true, completor = ChainCompletor.class, help = "The chain to run")
    protected String chain;

    @Argument(name = "doc", index = 1, required = false, completor = DocRefCompletor.class, help = "A reference to the new context document to use. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        try {
            Document doc = ctx.resolveDocument(path);
            OperationRequest request = ctx.getSession().newRequest(chain).setInput(doc);
            if (ctxVars != null) {
                for (String pair : ctxVars.split(sep)) {
                    String[] ar = StringUtils.split(pair, '=', true);
                    request.setContextProperty(ar[0], ar[1]);
                }
            }
            if (isVoid) {
                request.setHeader(Constants.HEADER_NX_VOIDOP, "true");
            }
            request.execute();

        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

}
