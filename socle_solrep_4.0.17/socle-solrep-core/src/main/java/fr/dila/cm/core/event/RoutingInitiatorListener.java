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
package fr.dila.cm.core.event;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class RoutingInitiatorListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();

        FeuilleRoute route = docCtx.getSourceDocument().getAdapter(FeuilleRoute.class);
        String initiator = (String) docCtx.getProperty(FeuilleRouteConstant.INITIATOR_EVENT_CONTEXT_KEY);
        CoreSession session = docCtx.getCoreSession();
        // initiator is a step validator
        route.setCanValidateStep(session, initiator);
        // initiator can see the route
        ACP acp = route.getDocument().getACP();
        ACL acl = acp.getOrCreateACL(FeuilleRouteConstant.ROUTING_ACL);
        acl.add(new ACE(initiator, SecurityConstants.READ, true));
        session.setACP(route.getDocument().getRef(), acp, true);
    }
}
