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
import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ConfigurationCommands extends CommandRegistry {

    public static final ConfigurationCommands INSTANCE = new ConfigurationCommands();

    public ConfigurationCommands() {
        super(GlobalCommands.INSTANCE, "config");
        addAnnotatedCommand(Settings.class);
    }

    @Override
    public String getTitle() {
        return "Configuration Commands";
    }

    @Override
    public String getDescription() {
        return "Commands for configuring the shell.";
    }

    @Override
    public String getPrompt(Shell shell) {
        return "config> ";
    }
}
