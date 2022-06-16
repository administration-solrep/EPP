/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 *
 * $Id$
 */

package fr.dila.cm.core.event;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Date;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener for mailbox creation events that sets the mailbox id according to
 * the mailbox type, and possibly owner in the case of a personal mailbox.
 * <p>
 * If mailbox id is already set (for instance when creating personal mailbox
 * using the casemanagement service), nothing is done.
 *
 * @author Anahide Tchertchian
 */
public class CreateMailboxIdListener implements EventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(CreateMailboxIdListener.class);
    private static final int MAX_CHARS_ID = 24;

    public void handleEvent(Event event) {
        DocumentEventContext docCtx;
        if (event.getContext() instanceof DocumentEventContext) {
            docCtx = (DocumentEventContext) event.getContext();
        } else {
            return;
        }

        CoreSession session = docCtx.getCoreSession();
        DocumentModel doc = docCtx.getSourceDocument();

        if (session != null && doc.hasFacet(MailboxConstants.MAILBOX_FACET)) {
            Mailbox mb = doc.getAdapter(Mailbox.class);
            if (mb.getId() != null) {
                return;
            }

            try {
                setIdForMailbox(session, mb);
            } catch (Exception e) {
                LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e);
            }
        }
    }

    private void setIdForMailbox(CoreSession session, Mailbox mb) {
        MailboxService mailboxService = ServiceUtil.getRequiredService(MailboxService.class);

        // set the mailbox id
        String id = null;
        if (MailboxConstants.type.personal.name().equals(mb.getType())) {
            String owner = mb.getOwner();
            if (owner == null) {
                LOGGER.warn(session, STLogEnumImpl.WARNING_TEC, "Création d'une mailbox personnelle sans propriétaire");
            } else {
                id = mailboxService.getUserPersonalMailboxId(owner);
            }
        }
        if (id == null) {
            String title = mb.getTitle();
            if (title != null) {
                id = IdUtils.generateId(title, "-", true, MAX_CHARS_ID);
                if (mailboxService.hasMailbox(session, id)) {
                    // add timestamp
                    id += "_" + new Date().getTime();
                }
            } else {
                id = String.valueOf(new Date().getTime());
            }
        }
        mb.setId(id);
    }
}
