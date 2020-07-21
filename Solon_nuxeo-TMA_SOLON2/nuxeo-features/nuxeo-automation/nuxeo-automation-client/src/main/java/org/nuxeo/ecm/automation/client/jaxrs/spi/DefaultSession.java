/* 
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_REQUEST_NOCHARSET;
import static org.nuxeo.ecm.automation.client.jaxrs.Constants.REQUEST_ACCEPT_HEADER;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blobs;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;
import org.nuxeo.ecm.automation.client.jaxrs.util.MultipartInput;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DefaultSession implements Session {

    protected final AbstractAutomationClient client;

    protected final Connector connector;

    protected final LoginInfo login;

    public DefaultSession(AbstractAutomationClient client, Connector connector,
            LoginInfo login) {
        this.client = client;
        this.connector = connector;
        this.login = login;
    }

    public AutomationClient getClient() {
        return client;
    }

    public Connector getConnector() {
        return connector;
    }

    public LoginInfo getLogin() {
        return login;
    }

    public <T> T getAdapter(Class<T> type) {
        return client.getAdapter(this, type);
    }

    public Object execute(OperationRequest request) throws Exception {
        Request req;
        String content = JsonMarshalling.writeRequest(request);
        String ctype;
        OperationInput input = request.getInput();
        if (input != null && input.isBinary()) {
            MultipartInput mpinput = new MultipartInput();
            mpinput.setRequest(content);
            ctype = mpinput.getContentType();
            if (input instanceof Blob) {
                Blob blob = (Blob) input;
                mpinput.setBlob(blob);
            } else if (input instanceof Blobs) {
                mpinput.setBlobs((Blobs) input);
            } else {
                throw new IllegalArgumentException(
                        "Unsupported binary input object: " + input);
            }
            req = new Request(Request.POST, request.getUrl(), mpinput);
        } else {
            req = new Request(Request.POST, request.getUrl(), content);
            ctype = CTYPE_REQUEST_NOCHARSET;
        }
        // set headers
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            req.put(entry.getKey(), entry.getValue());
        }
        req.put("Accept", REQUEST_ACCEPT_HEADER);
        req.put("Content-Type", ctype);
        return connector.execute(req);
    }

    public void execute(final OperationRequest request,
            final AsyncCallback<Object> cb) {
        client.asyncExec(new Runnable() {
            public void run() {
                try {
                    cb.onSuccess(execute(request));
                } catch (Throwable t) {
                    cb.onError(t);
                }
            }
        });
    }

    public Blob getFile(String path) throws Exception {
        Request req = new Request(Request.GET, client.getBaseUrl() + path);
        return (Blob) connector.execute(req);
    }

    public Blobs getFiles(String path) throws Exception {
        Request req = new Request(Request.GET, client.getBaseUrl() + path);
        return (Blobs) connector.execute(req);
    }

    public void getFile(final String path, final AsyncCallback<Blob> cb)
            throws Exception {
        client.asyncExec(new Runnable() {
            public void run() {
                try {
                    cb.onSuccess(getFile(path));
                } catch (Throwable t) {
                    cb.onError(t);
                }
            }
        });
    }

    public void getFiles(final String path, final AsyncCallback<Blobs> cb)
            throws Exception {
        client.asyncExec(new Runnable() {
            public void run() {
                try {
                    cb.onSuccess(getFiles(path));
                } catch (Throwable t) {
                    cb.onError(t);
                }
            }
        });
    }

    public OperationRequest newRequest(String id) throws Exception {
        return newRequest(id, new HashMap<String, String>());
    }

    public OperationRequest newRequest(String id, Map<String, String> ctx)
            throws Exception {
        OperationDocumentation op = getOperation(id);
        if (op == null) {
            throw new IllegalArgumentException("No such operation: " + id);
        }
        return new DefaultOperationRequest(this, op, ctx);
    }

    public OperationDocumentation getOperation(String id) {
        return client.getRegistry().getOperation(id);
    }

    public Map<String, OperationDocumentation> getOperations() {
        return client.getRegistry().getOperations();
    }

}
