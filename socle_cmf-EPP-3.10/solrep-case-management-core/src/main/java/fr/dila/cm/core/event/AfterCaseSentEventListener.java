/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package fr.dila.cm.core.event;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;

import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.event.CaseManagementEventConstants;

import static fr.dila.cm.event.CaseManagementEventConstants.EVENT_CONTEXT_CASE;
import static fr.dila.cm.event.CaseManagementEventConstants.EVENT_CONTEXT_IS_INITIAL;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 */
public class AfterCaseSentEventListener implements EventListener {

    @SuppressWarnings("unchecked")
    public void handleEvent(Event event) throws ClientException {
        Map<String, Serializable> properties = event.getContext().getProperties();
        Case envelope = (Case) properties.get(EVENT_CONTEXT_CASE);
        if (envelope == null) {
            return;
        }
        Map<String, List<String>> internalRecipients = (Map<String, List<String>>) properties.get(CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS);
        Map<String, List<String>> externalRecipients = (Map<String, List<String>>) properties.get(CaseManagementEventConstants.EVENT_CONTEXT_EXTERNAL_PARTICIPANTS);
        boolean isInitial = (Boolean) properties.get(EVENT_CONTEXT_IS_INITIAL);

        // Set Envelope recipients
        setRecipients(envelope, isInitial, internalRecipients, externalRecipients);
        envelope.save(event.getContext().getCoreSession());
    }

    protected void setRecipients(HasParticipants item, boolean isInitial,
            Map<String, List<String>> internalRecipients,
            Map<String, List<String>> externalRecipients) {

        if (isInitial) {
            item.addInitialInternalParticipants(internalRecipients);
            item.addInitialExternalParticipants(externalRecipients);
        }

        item.addParticipants(internalRecipients);
        item.addParticipants(externalRecipients);
    }

}
