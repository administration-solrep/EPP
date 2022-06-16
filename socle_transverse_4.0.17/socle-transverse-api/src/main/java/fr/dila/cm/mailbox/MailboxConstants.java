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

package fr.dila.cm.mailbox;

import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author Anahide Tchertchian
 *
 */
public class MailboxConstants {

    public enum type {
        personal,
        generic
    }

    public static final String MAILBOX_DOCUMENT_TYPE = "Mailbox";

    public static final String MAILBOX_FACET = "Mailbox";

    public static final String MAILBOX_ROOT_DOCUMENT_TYPE = "MailboxRoot";

    public static final String MAILBOX_SCHEMA = "mailbox";

    public static final String ID_FIELD = "mlbx:mailbox_id";

    public static final String TITLE_FIELD = "dc:title";

    public static final String DESCRIPTION_FIELD = "dc:description";

    public static final String TYPE_FIELD = "mlbx:type";

    public static final String OWNER_FIELD = "mlbx:owner";

    public static final String GROUPS_FIELD = "mlbx:groups";

    public static List<Mailbox> getMailboxList(List<DocumentModel> docs) {
        List<Mailbox> res = new ArrayList<Mailbox>();
        if (docs != null) {
            for (DocumentModel doc : docs) {
                res.add(doc.getAdapter(Mailbox.class));
            }
        }
        return res;
    }

    private MailboxConstants() {}
}
