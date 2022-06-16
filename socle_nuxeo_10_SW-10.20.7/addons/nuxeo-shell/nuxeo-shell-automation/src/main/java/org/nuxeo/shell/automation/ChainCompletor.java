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

import java.util.Collections;
import java.util.List;

import jline.Completor;

import org.nuxeo.ecm.automation.client.model.OperationDocumentation;
import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ChainCompletor implements Completor {

    protected RemoteContext ctx;

    public ChainCompletor() {
        this(Shell.get().getContextObject(RemoteContext.class));
    }

    public ChainCompletor(RemoteContext ctx) {
        this.ctx = ctx;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int complete(String buffer, int cursor, List clist) {
        if (buffer == null) {
            buffer = "";
        }
        for (OperationDocumentation op : ctx.getSession().getOperations().values()) {
            if ("Chain".equals(op.category)) {
                if (op.id.startsWith(buffer)) {
                    clist.add(op.id);
                }
            }
        }
        if (clist.isEmpty()) {
            return -1;
        }
        if (clist.size() == 1) {
            return 0;
        }
        Collections.sort(clist);
        return 0;
    }
}
