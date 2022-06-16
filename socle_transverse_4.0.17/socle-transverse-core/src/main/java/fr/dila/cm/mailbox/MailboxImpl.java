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
 *     Nicolas Ulrich
 *
 * $Id$
 */

package fr.dila.cm.mailbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Mailbox implementation using a document model as backend.
 *
 * @author Anahide Tchertchian
 */
public class MailboxImpl implements Mailbox {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(Mailbox.class);

    protected final DocumentModel doc;

    public MailboxImpl(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return doc;
    }

    protected String getStringProperty(String property) {
        return (String) doc.getPropertyValue(property);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getStringListProperty(String property) {
        List<String> res = null;
        Object propValue = doc.getPropertyValue(property);
        if (propValue instanceof List) {
            res = (List<String>) propValue;
        } else if (propValue instanceof String[]) {
            res = Arrays.asList((String[]) propValue);
        } else if (propValue != null) {
            throw new NuxeoException(String.format("Unexpected non-list value for prop %s: %s", property, propValue));
        }
        return res;
    }

    protected Calendar getDateProperty(String property) {
        return (Calendar) doc.getPropertyValue(property);
    }

    protected void setPropertyValue(String property, Serializable value) {
        doc.setPropertyValue(property, value);
    }

    @Override
    public String getDescription() {
        return getStringProperty(MailboxConstants.DESCRIPTION_FIELD);
    }

    @Override
    public List<String> getGroups() {
        return getStringListProperty(MailboxConstants.GROUPS_FIELD);
    }

    @Override
    public String getId() {
        return getStringProperty(MailboxConstants.ID_FIELD);
    }

    @Override
    public void setId(String id) {
        setPropertyValue(MailboxConstants.ID_FIELD, id);
    }

    @Override
    public String getOwner() {
        return getStringProperty(MailboxConstants.OWNER_FIELD);
    }

    @Override
    public String getTitle() {
        return getStringProperty(MailboxConstants.TITLE_FIELD);
    }

    @Override
    public String getType() {
        return getStringProperty(MailboxConstants.TYPE_FIELD);
    }

    @Override
    public void setDescription(String description) {
        setPropertyValue(MailboxConstants.DESCRIPTION_FIELD, description);
    }

    @Override
    public void setGroups(List<String> groups) {
        ArrayList<String> serializableGroups = new ArrayList<String>();
        if (groups != null) {
            serializableGroups.addAll(groups);
        }
        setPropertyValue(MailboxConstants.GROUPS_FIELD, serializableGroups);
    }

    @Override
    public void setOwner(String owner) {
        setPropertyValue(MailboxConstants.OWNER_FIELD, owner);
    }

    @Override
    public void setTitle(String title) {
        setPropertyValue(MailboxConstants.TITLE_FIELD, title);
    }

    @Override
    public void setType(MailboxConstants.type type) {
        setPropertyValue(MailboxConstants.TYPE_FIELD, type.name());
    }

    public int compareTo(Mailbox other) {
        // sort by type and then by title
        if (getType().equals(other.getType())) {
            // sort by title
            return getTitle().compareTo(other.getTitle());
        } else if (MailboxConstants.type.personal.name().equals(getType())) {
            return -1;
        } else {
            return 1;
        }
    }

    public void save(CoreSession session) {
        session.saveDocument(doc);
        session.save();
    }

    public String getParentId(CoreSession session) {
        try {
            if (session.hasPermission(doc.getParentRef(), SecurityConstants.READ)) {
                DocumentModel parent = session.getDocument(doc.getParentRef());
                Mailbox parentMailbox = parent.getAdapter(Mailbox.class);
                if (parentMailbox != null) {
                    return parentMailbox.getId();
                }
            }
        } catch (NuxeoException e) {
            log.error("Unable to retrieve parent mailbox id", e);
        }
        return null;
    }

    public List<String> getChildrenIds(CoreSession session) {
        List<String> res = new ArrayList<String>();
        try {
            if (session.hasPermission(doc.getRef(), SecurityConstants.READ_CHILDREN)) {
                DocumentModelList children = session.getChildren(doc.getRef());
                if (children != null) {
                    for (DocumentModel child : children) {
                        Mailbox childMailbox = child.getAdapter(Mailbox.class);
                        if (childMailbox != null) {
                            res.add(childMailbox.getId());
                        }
                    }
                }
            }
        } catch (NuxeoException e) {
            log.error("Unable to retrieve child mailbox ids", e);
        }
        return res;
    }

    @Override
    public List<String> getAllUsersAndGroups() {
        List<String> total = new ArrayList<String>();
        String owner = getOwner();
        if (owner != null) {
            total.add(owner);
        }

        List<String> groups = getGroups();
        if (groups != null) {
            total.addAll(groups);
        }
        return total;
    }
}
