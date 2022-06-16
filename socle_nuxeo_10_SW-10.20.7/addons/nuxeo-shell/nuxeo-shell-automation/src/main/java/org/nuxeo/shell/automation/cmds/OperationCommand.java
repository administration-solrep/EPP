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
import org.nuxeo.ecm.automation.client.RemoteException;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.Blobs;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.OperationDocumentation;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class OperationCommand implements Runnable {

    public static final String ATTR_VOID = "-void";

    public static final String ATTR_SCHEMAS = "-schemas";

    public static final String ATTR_CTX = "-ctx";

    protected Session session;

    protected Shell shell;

    protected OperationDocumentation op;

    protected OperationRequest request;

    protected OperationCommandType type;

    public OperationCommand() {
    }

    public void init(OperationCommandType type, Shell shell, OperationDocumentation op) {
        try {
            this.type = type;
            this.shell = shell;
            this.session = shell.getContextObject(Session.class);
            this.op = op;
            this.request = session.newRequest(op.id);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    public void run() {
        try {
            if (request.getInput() == null) {
                if (type.hasDocumentInput()) {
                    request.setInput(shell.getContextObject(RemoteContext.class).getDocument());
                }
            }
            Object result = request.execute();
            if (result instanceof Document) {
                Cat.print(shell.getConsole(), (Document) result);
            } else if (result instanceof Documents) {
                for (Document doc : (Documents) result) {
                    shell.getConsole().println(doc.getPath() + " - " + doc.getTitle());
                }
            } else if (result instanceof FileBlob) {
                shell.getConsole().println(((FileBlob) result).getFile().getAbsolutePath());
            } else if (result instanceof Blobs) {
                for (Blob blob : (Blobs) result) {
                    shell.getConsole().println(((FileBlob) blob).getFile().getAbsolutePath());
                }
            }
        } catch (RemoteException e) {
            throw new ShellException(e.getStatus() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    public void setParam(String name, Object value) {
        if (ATTR_SCHEMAS.equals(name)) {
            request.setHeader(Constants.HEADER_NX_SCHEMAS, (String) value);
        } else if (ATTR_VOID.equals(name)) {
            request.setHeader(Constants.HEADER_NX_VOIDOP, (String) value); // TODO
        } else if (ATTR_CTX.equals(name)) {
            for (String v : StringUtils.split(value.toString(), ',', true)) {
                String[] pair = StringUtils.split(v.toString(), '=', true);
                request.setContextProperty(pair[0], pair[1]);
            }
        } else {
            if (value instanceof String) {
                value = value.toString().replace("\\n", "\n");
                request.set(name, value);
            } else {
                request.set(name, value);
            }
        }
    }

}
