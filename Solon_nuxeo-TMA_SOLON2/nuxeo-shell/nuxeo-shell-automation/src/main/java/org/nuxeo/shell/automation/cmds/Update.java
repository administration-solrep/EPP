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
package org.nuxeo.shell.automation.cmds;

import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Command(name = "update", help = "Update document properties")
public class Update implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-s", hasValue = true, help = "Use this to change the separator used in properties. The default is ','")
    protected String sep = ",";

    @Argument(name = "properties", index = 0, help = "The propertis to update.")
    protected String props;

    @Argument(name = "path", index = 1, required = false, completor = DocRefCompletor.class, help = "The document path")
    protected String path;

    public void run() {
        DocRef doc = ctx.resolveRef(path);
        try {
            PropertyMap map = new PropertyMap();
            for (String pair : props.split(sep)) {
                String[] ar = StringUtils.split(pair, '=', true);
                map.set(ar[0], ar[1]);
            }
            ctx.getDocumentService().update(doc, map);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
