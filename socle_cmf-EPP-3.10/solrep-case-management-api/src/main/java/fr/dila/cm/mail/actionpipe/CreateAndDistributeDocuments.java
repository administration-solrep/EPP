/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:ldoguin@nuxeo.com">Laurent Doguin</a>
 *
 * $Id:$
 */

package fr.dila.cm.mail.actionpipe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.mail.action.ExecutionContext;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.contact.Contact;
import fr.dila.cm.contact.Contacts;
import fr.dila.cm.distribution.CMFDistributionInfo;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.MailboxManagementService;

/**
 * Transforms received email in a set of document models and distribute them.
 *
 * @author Laurent Doguin
 */
public class CreateAndDistributeDocuments extends
        AbstractCaseManagementMailAction {

    private static final Log log = LogFactory.getLog(CreateAndDistributeDocuments.class);

    @SuppressWarnings("unchecked")
    public boolean execute(ExecutionContext context) throws Exception {
        CoreSession session = getCoreSession(context);
        if (session == null) {
            log.error("Could not open core session");
            return false;
        }

        // retrieve metadata
        String senderEmail = (String) context.get(SENDER_EMAIL_KEY);
        Contacts origSenders = (Contacts) context.get(ORIGINAL_SENDERS_KEY);
        Contacts origToRecipients = (Contacts) context.get(ORIGINAL_TO_RECIPIENTS_KEY);
        Contacts origCcRecipients = (Contacts) context.get(ORIGINAL_CC_RECIPIENTS_KEY);

        MailboxManagementService mailboxManagemenetService = Framework.getService(MailboxManagementService.class);

        Mailbox senderMailbox = mailboxManagemenetService.getUserPersonalMailboxForEmail(
                session, senderEmail);
        if (senderMailbox == null) {
            // cannot find mailbox for user who forwarded => abort
            return false;
        }
        Contact senderContact = Contact.getContactForMailbox(senderMailbox,
                senderEmail, null, null);

        // gather sender/recipients info

        // senders
        Contacts internalOrigSenders = new Contacts();
        Contacts externalOrigSenders = new Contacts();
        fillContactInformation(session, mailboxManagemenetService, origSenders,
                internalOrigSenders, externalOrigSenders);
        List<String> origSendersMailboxesId = new LinkedList<String>();
        origSendersMailboxesId.addAll(internalOrigSenders.getMailboxes());

        // recipients for action
        Contacts internalOrigToRecipients = new Contacts();
        Contacts externalOrigToRecipients = new Contacts();
        fillContactInformation(session, mailboxManagemenetService,
                origToRecipients, internalOrigToRecipients,
                externalOrigToRecipients);
        Set<String> mailboxesForAction = new LinkedHashSet<String>();
        mailboxesForAction.addAll(internalOrigToRecipients.getMailboxes());

        // add sender personal mailbox
        mailboxesForAction.add(senderMailbox.getId());
        internalOrigToRecipients.add(senderContact);

        // recipients for information
        Contacts internalOrigCcRecipients = new Contacts();
        Contacts externalOrigCcRecipients = new Contacts();
        fillContactInformation(session, mailboxManagemenetService,
                origCcRecipients, internalOrigCcRecipients,
                externalOrigCcRecipients);
        Set<String> mailboxesForInformation = new LinkedHashSet<String>();
        mailboxesForInformation.addAll(internalOrigCcRecipients.getMailboxes());

        CMFDistributionInfo distributionInfo = new CMFDistributionInfo();
        distributionInfo.setForActionMailboxes(new ArrayList<String>(
                mailboxesForAction));
        distributionInfo.setForInformationMailboxes(new ArrayList<String>(
                mailboxesForInformation));              

        // save changes to core
        session.save();

        return true;
    }

    protected void fillContactInformation(CoreSession session,
            MailboxManagementService mailboxService, Contacts originalContacts,
            Contacts internalContacts, Contacts externalContacts) {
        if (originalContacts != null) {
            for (Contact origContact : originalContacts) {
                String origContactEmail = origContact.getEmail();
                Mailbox origContactMailbox = mailboxService.getUserPersonalMailboxForEmail(
                        session, origContactEmail);
                if (origContactMailbox != null) {
                    Contact newOrigSender = Contact.getContactForMailbox(
                            origContactMailbox, origContactEmail, null, null);
                    if (!internalContacts.contains(newOrigSender)) {
                        internalContacts.add(newOrigSender);
                    }
                } else {
                    if (!externalContacts.contains(origContact)) {
                        externalContacts.add(origContact);
                    }
                }
            }
        }
    }

    public void reset(ExecutionContext context) throws Exception {
        // do nothing
    }

}
