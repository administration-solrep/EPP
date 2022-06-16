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
package fr.dila.cm.cases;

import fr.dila.cm.mailbox.Mailbox;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author arussel
 */
public class HasParticipantsImpl implements HasParticipants {
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    enum DistributionType {
        FOR_ACTION(
            "all_action_participant_mailboxes",
            "initial_action_external_participant_mailboxes",
            "initial_action_internal_participant_mailboxes"
        );

        private String allRecipientsProperty;
        private String externalRecipientsProperty;
        private String internalRecipientsProperty;

        private DistributionType(
            String allRecipientsProperty,
            String externalRecipientsProperty,
            String internalRecipientsProperty
        ) {
            this.allRecipientsProperty = allRecipientsProperty;
            this.externalRecipientsProperty = externalRecipientsProperty;
            this.internalRecipientsProperty = internalRecipientsProperty;
        }

        public static final String getInternalProperty(String key) {
            return DistributionType.valueOf(key).internalRecipientsProperty;
        }

        public static final String getExternalProperty(String key) {
            return DistributionType.valueOf(key).externalRecipientsProperty;
        }

        public static final String getAllProperty(String key) {
            return DistributionType.valueOf(key).allRecipientsProperty;
        }

        public static final String getInternalProperty(DistributionType type) {
            return type.internalRecipientsProperty;
        }

        public static final String getExternalProperty(DistributionType type) {
            return type.externalRecipientsProperty;
        }

        public static final String getAllProperty(DistributionType type) {
            return type.allRecipientsProperty;
        }
    }

    public HasParticipantsImpl(DocumentModel document) {
        this.document = document;
    }

    public void addInitialInternalParticipants(Map<String, List<String>> recipients) {
        if (recipients == null) {
            return;
        }

        for (String key : recipients.keySet()) {
            String schemaProperty = DistributionType.getInternalProperty(key);
            addRecipients(recipients.get(key), schemaProperty);
        }
    }

    public void addInitialExternalParticipants(Map<String, List<String>> recipients) {
        if (recipients == null) {
            return;
        }

        for (String key : recipients.keySet()) {
            String schemaProperty = DistributionType.getExternalProperty(key);
            addRecipients(recipients.get(key), schemaProperty);
        }
    }

    public void addParticipants(Map<String, List<String>> recipients) {
        if (recipients == null) {
            return;
        }

        for (String key : recipients.keySet()) {
            String schemaProperty = DistributionType.getAllProperty(key);
            addRecipients(recipients.get(key), schemaProperty);
        }
    }

    public Map<String, List<String>> getAllParticipants() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();

        for (DistributionType key : DistributionType.values()) {
            List<String> recipients = getRecipients(DistributionType.getAllProperty(key));
            values.put(key.name(), recipients);
        }

        return values;
    }

    public Map<String, List<String>> getInitialInternalParticipants() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();

        for (DistributionType key : DistributionType.values()) {
            List<String> recipients = getRecipients(DistributionType.getInternalProperty(key));
            values.put(key.name(), recipients);
        }

        return values;
    }

    public Map<String, List<String>> getInitialExternalParticipants() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();

        for (DistributionType key : DistributionType.values()) {
            List<String> recipients = getRecipients(DistributionType.getExternalProperty(key));
            values.put(key.name(), recipients);
        }

        return values;
    }

    protected void addRecipients(List<String> recipients, String xpath) {
        // get list of old + new
        List<String> oldIds = getRecipients(xpath);
        if (oldIds == null) {
            oldIds = new ArrayList<String>();
        }
        for (String newId : recipients) {
            if (!oldIds.contains(newId)) {
                oldIds.add(newId);
            }
        }

        document.setPropertyValue(xpath, (Serializable) oldIds);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<String> getRecipients(String recipientsXpath) {
        List<String> recipients = null;
        recipients = (List) document.getPropertyValue(recipientsXpath);

        return recipients;
    }

    protected List<String> getMailboxIds(List<Mailbox> list) {
        List<String> result = new ArrayList<String>();
        for (Mailbox mailbox : list) {
            result.add(mailbox.getId());
        }
        return result;
    }
}
