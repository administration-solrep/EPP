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

import java.util.Map;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.AutomationFeature;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.cmds.GlobalCommands;
import org.nuxeo.shell.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class RemoteCommands extends CommandRegistry {

    public static final RemoteCommands INSTANCE = new RemoteCommands();

    public RemoteCommands() {
        super(GlobalCommands.INSTANCE, "remote");
        onDisconnect();
    }

    public void onConnect() {
        addAnnotatedCommand(Disconnect.class);
        addAnnotatedCommand(Ls.class);
        addAnnotatedCommand(Cd.class);
        addAnnotatedCommand(Pwd.class);
        addAnnotatedCommand(Popd.class);
        addAnnotatedCommand(Pushd.class);
        addAnnotatedCommand(MkDir.class);
        addAnnotatedCommand(Update.class);
        addAnnotatedCommand(Rm.class);
        addAnnotatedCommand(Query.class);
        addAnnotatedCommand(Cat.class);
        addAnnotatedCommand(Tree.class);
        addAnnotatedCommand(Script.class);
        addAnnotatedCommand(SetBlob.class);
        addAnnotatedCommand(GetBlob.class);
        addAnnotatedCommand(GetBlobs.class);
        addAnnotatedCommand(RemoveBlob.class);
        addAnnotatedCommand(RunChainWithDoc.class);
        addAnnotatedCommand(RunChainWithFile.class);
        addAnnotatedCommand(MkRelation.class);
        addAnnotatedCommand(GetRelations.class);
        addAnnotatedCommand(SetProperty.class);
        addAnnotatedCommand(GetProperty.class);
        addAnnotatedCommand(Lock.class);
        addAnnotatedCommand(Unlock.class);
        addAnnotatedCommand(Cp.class);
        addAnnotatedCommand(Mv.class);
        addAnnotatedCommand(Rename.class);
        addAnnotatedCommand(Publish.class);
        addAnnotatedCommand(Perms.class);
        addAnnotatedCommand(LifeCycleState.class);
        addAnnotatedCommand(Fire.class);
        addAnnotatedCommand(Audit.class);
    }

    public void onDisconnect() {
        clear();
        addAnnotatedCommand(Connect.class);
    }

    @Override
    public String getPrompt(Shell shell) {
        RemoteContext ctx = shell.getContextObject(RemoteContext.class);
        if (ctx == null) {
            return "remote> ";
        }
        Document doc = ctx.getDocument();
        Path path = new Path(doc.getPath());
        String p = path.isRoot() ? "/" : path.lastSegment();
        return ctx.getUserName() + "@" + ctx.getHost() + ":" + p + "> ";
    }

    @Override
    public String getTitle() {
        return "Nuxeo Server Commands";
    }

    @Override
    public String getDescription() {
        return "High level commands exposed by a remote Nuxeo Server";
    }

    @Override
    public void autorun(Shell shell) {
        // check if connection info is already available and connect to remote
        // if so.
        Map<String, String> args = (Map<String, String>) shell.getMainArguments();
        if (args != null) {
            String url = args.get("#1");
            String username = args.get("-u");
            String password = args.get("-p");
            String dir = args.get("-d");
            if (dir == null) {
                dir = "/";
            }
            if (url != null && username != null && password != null) {
                try {
                    shell.getConsole().println("Connecting to " + url + " ...");
                    shell.getFeature(AutomationFeature.class).connect(url, username, password, dir);
                } catch (Throwable t) {
                    throw new ShellException("Failed to connect to " + url, t);
                }
            }
        }
    }

}
