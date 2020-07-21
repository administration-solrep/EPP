/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     arussel
 */
package org.nuxeo.ecm.platform.ui.web.shield;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.RegistrationInfo;

/**
 * @author arussel
 */
public class ErrorPageForwarder {

    private static final Log nuxeoErrorLog = LogFactory.getLog("nuxeo-error-log");
    private static final Log log = LogFactory.getLog(ErrorPageForwarder.class);

    private static final String SEAM_MESSAGES = "org.jboss.seam.international.messages";

    private ServletContext servletContext;

    public void forwardToErrorPage(HttpServletRequest request,
            HttpServletResponse response, Throwable t, String exceptionMessage,
            String userMessage, Boolean securityError,
            ServletContext servletContext) throws ServletException, IOException {
        forwardToErrorPage(request, response, getStackTraceAsString(t),
                exceptionMessage, userMessage, securityError, servletContext);
    }

    public void forwardToErrorPage(HttpServletRequest request,
            HttpServletResponse response, String stackTrace,
            String exceptionMessage, String userMessage, Boolean securityError,
            ServletContext servletContext) throws ServletException, IOException {
        log.error(stackTrace);
        this.servletContext = servletContext;
        // cut/paste from seam Exception filter.
        // we recreate the seam context to be able to use the messages.
        MockFacesContext facesContext = createFacesContext(request, response);
        facesContext.setCurrent();

        // if the event context was cleaned up, fish the conversation id
        // directly out of the ServletRequest attributes, else get it from
        // the event context
        Manager manager = Contexts.isEventContextActive()
                ? (Manager) Contexts.getEventContext().get(Manager.class)
                : (Manager) request.getAttribute(Seam.getComponentName(Manager.class));
        String conversationId = manager == null ? null
                : manager.getCurrentConversationId();
        FacesLifecycle.beginExceptionRecovery(facesContext.getExternalContext());
        // If there is an existing long-running conversation on
        // the failed request, propagate it
        if (conversationId == null) {
            Manager.instance().initializeTemporaryConversation();
        } else {
            ConversationPropagation.instance().setConversationId(conversationId);
            Manager.instance().restoreConversation();
        }
        // we get the message from the seam attribute as the EL won't work in
        // xhtml
        String user_message = request.getAttribute(SEAM_MESSAGES) == null ? "An unexpected error occurred."
                : (String) ((Map) request.getAttribute(SEAM_MESSAGES)).get(userMessage);
        FacesLifecycle.beginExceptionRecovery(facesContext.getExternalContext());
        request.setAttribute("exception_message", exceptionMessage);
        request.setAttribute("user_message", user_message);
        request.setAttribute("stackTrace", stackTrace);
        request.setAttribute("securityError", securityError);
        request.setAttribute("request_dump", getRequestDump(request));
        request.getRequestDispatcher("/nuxeo_error.jsp").forward(request,
                response);
        //FacesLifecycle.endRequest( facesContext.getExternalContext() );
        //facesContext.release();
    }

    @SuppressWarnings("unchecked")
    private String getRequestDump(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nParameter:\n");
        Map<String, String[]> m = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : m.entrySet()) {
            builder.append(entry.getKey()).append(":");
            if (entry.getValue() == null) {
                continue;
            }
            for (String s : entry.getValue()) {
                builder.append(s).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("\n");
        }
        builder.append("\n");
        Enumeration<String> names = request.getAttributeNames();
        builder.append("Attributes:\n");
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.equals(SEAM_MESSAGES)) {
                continue;
            }
            Object obj = request.getAttribute(name);
            builder.append(name).append(": ").append(obj.toString()).append("\n");
        }
        builder.append("\n");
        Collection<RegistrationInfo> infos = Framework.getRuntime().getComponentManager().getRegistrations();
        builder.append("Components:\n");
        for (RegistrationInfo info : infos) {
            builder.append(info.getComponent().getName()).append(",")
                    .append(info.isActivated() ? "activated" : "not activated").append("\n");
        }
        nuxeoErrorLog.trace("User Principal: " + request.getUserPrincipal() + "\n" + builder.toString());
        return builder.toString();
    }

    private MockFacesContext createFacesContext(HttpServletRequest request,
            HttpServletResponse response) {
        MockFacesContext mockFacesContext = new MockFacesContext(
                new MockExternalContext(servletContext, request, response),
                new MockApplication());
        mockFacesContext.setViewRoot(new UIViewRoot());
        return mockFacesContext;
    }

    public String getStackTraceAsString(Throwable t) {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter);
        t.printStackTrace(pwriter);
        return swriter.getBuffer().toString();
    }

}
