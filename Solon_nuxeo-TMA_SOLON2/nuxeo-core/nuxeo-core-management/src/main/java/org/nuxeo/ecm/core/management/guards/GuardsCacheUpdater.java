/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     "Stephane Lacoin at Nuxeo (aka matic)"
 */
package org.nuxeo.ecm.core.management.guards;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.management.api.AdministrativeStatusManager;

/**
 * Listen to administrative status changes and cache
 * state in a map.
 *
 * @author "Stephane Lacoin at Nuxeo (aka matic)"
 */
public class GuardsCacheUpdater implements EventListener {
    @Override
    public void handleEvent(Event event) throws ClientException {
        EventContext ctx = event.getContext();
        String category = (String) ctx.getProperty("category");
        if (!AdministrativeStatusManager.ADMINISTRATIVE_EVENT_CATEGORY.equals(category)) {
            return;
        }
        String id = (String) ctx.getProperty(AdministrativeStatusManager.ADMINISTRATIVE_EVENT_SERVICE);
        if (AdministrativeStatusManager.ACTIVATED_EVENT.equals(event.getName())) {
            GuardedServiceProvider.INSTANCE.activeStatuses.put(id, Boolean.TRUE);
        } else if (AdministrativeStatusManager.PASSIVATED_EVENT.equals(event.getName())) {
            GuardedServiceProvider.INSTANCE.activeStatuses.put(id, Boolean.FALSE);
        }
    }
}
