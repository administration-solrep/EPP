/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 * $Id$
 */

package org.nuxeo.ecm.core.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.model.NoSuchRepositoryException;
import org.nuxeo.ecm.core.model.Repository;
import org.nuxeo.ecm.core.model.Session;

/**
 * {@link JBossRepositoryMBean} implementation.
 *
 * @author bstefanescu
 */
public class JBossRepository implements JBossRepositoryMBean {

    protected final String name;

    public JBossRepository(String name) {
        this.name = name;
    }

    public Repository getRepository() throws NoSuchRepositoryException {
        return NXCore.getRepository(name);
    }

    @Override
    public String listDocumentLocks() {
        return "Not Implemented";
    }

    @Override
    public String listOpenedSessions() throws NoSuchRepositoryException, DocumentException {
        Repository repo = getRepository();
        Session[] sessions = repo.getOpenedSessions();
        StringBuilder buf = new StringBuilder();
        buf.append("<b>Started sessions count: </b>").append(repo.getStartedSessionsCount()).append("<br>");
        buf.append("<b>Closed sessions count: </b>").append(repo.getClosedSessionsCount()).append("<br>");
        buf.append("<b>Active sessions count: </b>").append(repo.getActiveSessionsCount()).append("<br>");
        buf.append("<ol>");
        for (Session session : sessions) {
            Map<String, Serializable> ctx = session.getSessionContext();
            buf.append("<li>").append("Session #").append(session.getSessionId())
                .append("; principal: ").append(ctx.get("principal"))
                .append("; ctime: ").append(new Date((Long) ctx.get("creationTime")));
        }
        buf.append("</ol>");
        return buf.toString();
    }

    @Override
    public String listRegisteredDocumentTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String listRegisteredSchemas() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String listRegisteredTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void restart(boolean reloadTypes) {
        // TODO Auto-generated method stub
    }

}
