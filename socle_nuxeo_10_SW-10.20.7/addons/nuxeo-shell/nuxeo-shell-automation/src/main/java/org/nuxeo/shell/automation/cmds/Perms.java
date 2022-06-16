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

import java.util.HashMap;

import jline.ANSIBuffer;

import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.automation.Scripting;
import org.nuxeo.shell.utils.ANSICodes;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "perms", help = "Set or view permissions on a document")
public class Perms implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-acl", hasValue = true, help = "The ACL to view or modify. If not specified then in write mode the local ACL is used and in view mode all acls are printed.")
    protected String acl;

    @Parameter(name = "-remove", hasValue = false, help = "Remove the given acl.")
    protected boolean remove;

    @Parameter(name = "-grant", hasValue = true, help = "If used the ACL will be modified by granting the specified permission on the specified user. The grant value format is \"user:permission\".")
    protected String grant;

    @Parameter(name = "-deny", hasValue = true, help = "If used the ACL will be modified by denying the specified permission on the specified user. The deny value format is \"user:permission\".")
    protected String deny;

    @Argument(name = "doc", index = 0, required = false, completor = DocRefCompletor.class, help = "The target document. If not specified the current document is used. To use UID references prefix them with 'doc:'.")
    protected String path;

    public void run() {
        DocRef doc = ctx.resolveRef(path);
        try {
            if (remove) {
                removeAcl(doc, acl);
            } else if (grant == null && deny == null) {
                printAcl(ctx.getShell().getConsole(), doc, acl);
            } else {
                setAcl(doc, acl, grant, deny);
            }
        } catch (ShellException e) {
            throw e;
        } catch (Exception e) {
            throw new ShellException("Failed to set property on " + doc, e);
        }
    }

    protected void setAcl(DocRef doc, String acl, String grant, String deny) throws Exception {
        if (grant != null) {
            String[] ar = StringUtils.split(grant, ':', true);
            if (ar.length != 2) {
                throw new ShellException("Invalid grant expression: Use \"user:permission\".");
            }
            ctx.getDocumentService().setPermission(doc, ar[0], ar[1], acl, true);
        }
        if (deny != null) {
            String[] ar = StringUtils.split(deny, ':', true);
            if (ar.length != 2) {
                throw new ShellException("Invalid deny expression: Use \"user:permission\".");
            }
            ctx.getDocumentService().setPermission(doc, ar[0], ar[1], acl, false);
        }
    }

    protected void printAcl(ShellConsole console, DocRef doc, String acl) throws Exception {
        HashMap<String, Object> ctx = new HashMap<String, Object>();
        if (acl != null) {
            ctx.put("acl", acl);
        }
        ctx.put("ref", doc.toString());
        String result = Scripting.run("scripts/printAcl.groovy", ctx, null);
        ANSIBuffer buf = Shell.get().newANSIBuffer();
        ANSICodes.appendTemplate(buf, result, false);
        console.println(buf.toString());
    }

    protected void removeAcl(DocRef doc, String acl) throws Exception {
        if (acl == null) {
            throw new ShellException("In remove mode the -acl parameter is required!");
        }
        ctx.getDocumentService().removeAcl(doc, acl);
    }

}
