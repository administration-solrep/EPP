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

import java.util.List;

import jline.SimpleCompletor;

import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocTypeCompletor extends SimpleCompletor {

    protected RemoteContext ctx;

    public DocTypeCompletor() {
        this(Shell.get().getContextObject(RemoteContext.class));
    }

    public DocTypeCompletor(RemoteContext ctx) {
        super(new String[0]);
        this.ctx = ctx;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int complete(String buffer, int cursor, List clist) {
        String[] names = new String[] { "Workspace", "Section", "Folder", "File", "Note" };
        setCandidateStrings(names);
        return super.complete(buffer, cursor, clist);
    }

}
