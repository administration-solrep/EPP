/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell.automation;

import java.io.InputStream;

import jline.Completor;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.CommandType;
import org.nuxeo.shell.CompletorProvider;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.ShellFeature;
import org.nuxeo.shell.ValueAdapter;
import org.nuxeo.shell.automation.cmds.OperationCommandType;
import org.nuxeo.shell.automation.cmds.RemoteCommands;
import org.nuxeo.shell.cmds.GlobalCommands;

/**
 * The automation feature is providing connection with Nuxeo servers through
 * automation service and remote commands based on operations.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class AutomationFeature implements ShellFeature, ValueAdapter,
        CompletorProvider {

    public static final String AUTOMATION_NS = "automation";

    protected RemoteContext ctx;

    public void install(Shell shell) {
        shell.putContextObject(AutomationFeature.class, this);
        shell.addCompletorProvider(this);
        shell.addValueAdapter(this);
        shell.addRegistry(RemoteCommands.INSTANCE);
        shell.getVersions().add("Nuxeo Server Minimal Version: "+getNuxeoServerVersion());
    }

    public HttpAutomationClient connect(String url, String username,
            String password, String initialDirectory) throws Exception {
        if (isConnected()) {
            disconnect();
        }
        Shell shell = Shell.get();
        HttpAutomationClient client = new HttpAutomationClient(url);
        Session session = client.getSession(username, password);
        ctx = new RemoteContext(this, client, session, initialDirectory);

        // switch to automation command namespace
        RemoteCommands.INSTANCE.onConnect();
        CommandRegistry reg = new AutomationRegistry();
        // build automation registry
        reg.addAnnotatedCommand(PrintOperation.class);
        buildCommands(reg, session);
        shell.addRegistry(reg);

        shell.setActiveRegistry(RemoteCommands.INSTANCE.getName());
        return client;
    }

    public boolean isConnected() {
        return ctx != null;
    }

    protected void buildCommands(CommandRegistry reg, Session session) {
        for (OperationDocumentation op : session.getOperations().values()) {
            if (!"Seam".equals(op.requires)) {
                OperationCommandType type = OperationCommandType.fromOperation(op);
                reg.addCommandType(type);// TODO
            }
        }
    }

    public void disconnect() {
        if (ctx != null) {
            ctx.getClient().shutdown();
            ctx.dispose();
            Shell shell = Shell.get();
            // shell.setActiveRegistry(FileSystemCommands.INSTANCE.getName());
            RemoteCommands.INSTANCE.onDisconnect();
            shell.removeRegistry(AUTOMATION_NS);
            ctx = null;
        }
    }

    public HttpAutomationClient getClient() {
        return ctx.getClient();
    }

    public Session getSession() {
        return ctx.getSession();
    }

    public RemoteContext getContext() {
        return ctx;
    }

    public Completor getCompletor(Shell shell, CommandType cmd, Class<?> type) {
        if (DocRef.class.isAssignableFrom(type)) {
            return new DocRefCompletor(ctx);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Shell shell, Class<T> type, String value) {
        if (type == DocRef.class) {
            return (T) ctx.resolveRef(value);
        }
        return null;
    }

    static class AutomationRegistry extends CommandRegistry {
        public AutomationRegistry() {
            super(GlobalCommands.INSTANCE, AUTOMATION_NS);
        }

        @Override
        public String getTitle() {
            return "Nuxeo Automation Commands";
        }

        @Override
        public String getDescription() {
            return "Commands exposed by the Nuxeo Server through automation";
        }
    }

    public static String getNuxeoServerVersion() {
        try {
            InputStream in = AutomationFeature.class.getClassLoader().getResourceAsStream("META-INF/nuxeo.version");
            if (in != null) {
                return org.nuxeo.shell.fs.FileSystem.read(in).trim();
            } else {
                return "Unknown";
            }
        } catch (Exception e) {
            throw new ShellException("Failed to read server version", e);
        }
    }

}
