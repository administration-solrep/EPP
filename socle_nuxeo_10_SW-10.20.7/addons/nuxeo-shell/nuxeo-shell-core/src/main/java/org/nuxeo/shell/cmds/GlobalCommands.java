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
package org.nuxeo.shell.cmds;

import org.nuxeo.shell.CommandRegistry;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class GlobalCommands extends CommandRegistry {

    public static final GlobalCommands INSTANCE = new GlobalCommands();

    public GlobalCommands() {
        super(null, "global");
        addAnnotatedCommand(Interactive.class);
        addAnnotatedCommand(Help.class);
        addAnnotatedCommand(Commands.class);
        addAnnotatedCommand(Exit.class);
        addAnnotatedCommand(Use.class);
        addAnnotatedCommand(Trace.class);
        addAnnotatedCommand(Version.class);
        addAnnotatedCommand(Settings.class);
        addAnnotatedCommand(Install.class);
    }

    @Override
    public String getTitle() {
        return "Built-in Commands";
    }

    @Override
    public String getDescription() {
        return "Basic commands provided by the shell";
    }

}
