/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */
package fr.dila.ecm.platform.routing.core.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class EventFirer {

    static public void fireEvent(CoreSession coreSession,
            DocumentRouteElement element,
            Map<String, Serializable> eventProperties, String eventName) {
        if (eventProperties == null) {
            eventProperties = new HashMap<String, Serializable>();
        }
        eventProperties.put(
                DocumentRoutingConstants.DOCUMENT_ELEMENT_EVENT_CONTEXT_KEY,
                element);
        eventProperties.put(DocumentEventContext.CATEGORY_PROPERTY_KEY,
                DocumentRoutingConstants.ROUTING_CATEGORY);
        DocumentEventContext envContext = new DocumentEventContext(coreSession,
                coreSession.getPrincipal(), element.getDocument());
        envContext.setProperties(eventProperties);
        try {
            getEventProducer().fireEvent(envContext.newEvent(eventName));
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    static protected EventProducer getEventProducer() {
        try {
            return Framework.getService(EventProducer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
