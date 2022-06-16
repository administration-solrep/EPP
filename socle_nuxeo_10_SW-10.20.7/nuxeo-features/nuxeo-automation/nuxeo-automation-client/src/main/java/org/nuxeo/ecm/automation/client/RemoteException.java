/*
 * (C) Copyright 2006-2014 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.ecm.automation.client;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class RemoteException extends AutomationException {

    private static final long serialVersionUID = 1L;

    protected final int status;

    protected final String type;

    protected final String info;

    protected final Throwable remoteCause;

    public RemoteException(int status, String type, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.type = type;
        info = extractInfo(cause);
        remoteCause = cause;
    }

    public RemoteException(int status, String type, String message, String info) {
        super(message);
        this.status = status;
        this.type = type;
        this.info = info;
        remoteCause = null;
    }

    public int getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    protected static String extractInfo(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public Throwable getRemoteCause() {
        return remoteCause;
    }

    public String getRemoteStackTrace() {
        return status + " - " + getMessage() + "\n" + info;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        s.println("====== Remote Stack Trace:");
        s.print(getRemoteStackTrace());
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        s.println("====== Remote Stack Trace:");
        s.print(getRemoteStackTrace());
    }

    public static RemoteException wrap(Throwable t) {
        return wrap(t, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    public static RemoteException wrap(Throwable t, int status) {
        return wrap(t.getMessage(), t, status);
    }

    public static RemoteException wrap(String message, Throwable t) {
        return wrap(message, t, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    public static RemoteException wrap(String message, Throwable t, int status) {
        RemoteException e = new RemoteException(status, t.getClass().getName(), message, t);
        e.initCause(t);
        return e;
    }

}
