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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.StreamBlob;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.fs.FileSystem;

/**
 * Helper class to run remote scripts.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Scripting {

    public static String run(File script, Map<String, Object> args, Integer timeout) throws IOException {
        FileInputStream in = new FileInputStream(script);
        try {
            return run(script.getName(), in, args, timeout);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }

    }

    public static String run(String resource, Map<String, Object> args, Integer timeout) throws IOException {
        InputStream in = Scripting.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new FileNotFoundException("No such resource: " + resource);
        }
        try {
            return run(resource, in, args, timeout);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public static String run(URL url, Map<String, Object> args, Integer timeout) throws IOException {
        InputStream in = url.openStream();
        try {
            return run(url.getFile(), in, args, timeout);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public static String run(String name, InputStream in, Map<String, Object> args, Integer timeout) {
        try {
            return runScript(Shell.get().getContextObject(RemoteContext.class), new StreamBlob(in, name, "text/plain"),
                    args, timeout);
        } catch (ShellException e) {
            throw e;
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    public static String runScript(RemoteContext ctx, Blob blob, Map<String, Object> args, Integer timeout)
            throws Exception {
        String fname = blob.getFileName();
        if (fname != null) {
            if (fname.endsWith(".groovy")) {
                fname = "groovy";
            } else {
                fname = null;
            }
        }
        if (args == null) {
            args = new HashMap<String, Object>();
        }
        OperationRequest req = ctx.getSession().newRequest("Context.RunInputScript", args).setInput(blob);

        if (timeout != null) {
            req.setHeader("Nuxeo-Transaction-Timeout", "" + timeout * 1000);
        }
        if (fname != null) {
            req.set("type", fname);
        }
        Blob response = (Blob) req.execute();
        if (response != null) {
            InputStream in = response.getStream();
            String str = null;
            try {
                str = FileSystem.readContent(in);
            } finally {
                in.close();
                if (response instanceof FileBlob) {
                    ((FileBlob) response).getFile().delete();
                }
            }
            return str;
        }
        return null;
    }

}
