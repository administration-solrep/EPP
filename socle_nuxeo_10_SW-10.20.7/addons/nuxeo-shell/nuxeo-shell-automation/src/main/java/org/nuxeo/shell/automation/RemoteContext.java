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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.client.AutomationClient;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PathRef;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class RemoteContext {

    protected Shell shell;

    protected AutomationFeature feature;

    protected HttpAutomationClient client;

    protected Session session;

    protected DocumentService ds;

    protected Document doc;

    protected List<Document> stack;

    protected String userName;

    protected String host;

    public RemoteContext(AutomationFeature feature, HttpAutomationClient client, Session session, String dir)
            throws Exception {
        this.shell = Shell.get();
        this.client = client;
        this.session = session;
        ds = session.getAdapter(DocumentService.class);
        stack = new ArrayList<Document>();
        try {
            doc = ds.getDocument(dir);
        } catch (Throwable e) {
            throw new ShellException("Cannot access to " + dir + " (use another directory using the -d parameter)", e);
        }
        userName = session.getLogin().getUsername();
        host = new URL(client.getBaseUrl()).getHost();
        shell.putContextObject(RemoteContext.class, this);
        shell.putContextObject(AutomationClient.class, client);
        shell.putContextObject(Session.class, session);
        shell.putContextObject(DocumentService.class, ds);
    }

    public String getUserName() {
        return userName;
    }

    public String getHost() {
        return host;
    }

    public void dispose() {
        shell.removeContextObject(RemoteContext.class);
        shell.removeContextObject(AutomationClient.class);
        shell.removeContextObject(Session.class);
        shell.removeContextObject(DocumentService.class);
    }

    public Shell getShell() {
        return shell;
    }

    public AutomationFeature getFeature() {
        return feature;
    }

    public HttpAutomationClient getClient() {
        return client;
    }

    public Session getSession() {
        return session;
    }

    public Document getDocument() {
        return doc;
    }

    public Path resolvePath(String path) {
        if (path == null) {
            return new Path(getDocument().getPath());
        }
        if (path.startsWith("/")) {
            return new Path(path);
        } else {
            return new Path(doc.getPath()).append(path);
        }
    }

    public DocRef resolveRef(String path) {
        if (path == null) {
            return getDocument();
        }
        if (path.startsWith("doc:")) {
            return DocRef.newRef(path.substring("doc:".length()));
        }
        return new PathRef(resolvePath(path).toString());
    }

    public Document resolveDocument(String path) {
        return resolveDocument(path, null);
    }

    public Document resolveDocument(String path, String schemas) {
        DocRef ref = null;
        try {
            ref = resolveRef(path);
            return ds.getDocument(ref, schemas);
        } catch (Exception e) {
            throw new ShellException("Failed to fetch document: " + ref);
        }
    }

    public void setDocument(Document doc) {
        this.doc = doc;
    }

    public Document peekDocument() {
        if (!stack.isEmpty()) {
            return stack.get(stack.size() - 1);
        }
        return null;
    }

    public Document pushDocument(Document doc) {
        setDocument(doc);
        Document old = peekDocument();
        stack.add(doc);
        return old;
    }

    public Document popDocument() {
        if (!stack.isEmpty()) {
            Document doc = stack.remove(stack.size() - 1);
            setDocument(peekDocument());
            return doc;
        }
        return null;
    }

    public DocumentService getDocumentService() {
        return ds;
    }

    public List<Document> getStack() {
        return stack;
    }
}
