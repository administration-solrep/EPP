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
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Mailbox interface
 *
 * @author Anahide Tchertchian
 */
public interface Mailbox extends Serializable, Comparable<Mailbox> {
    /**
     * Returns the document model representing this mailbox.
     */
    DocumentModel getDocument();

    /**
     * Returns the mailbox identifier.
     */
    String getId();

    /**
     * Sets id of the mailbox.
     */
    void setId(String id);

    /**
     * Returns title of the mailbox.
     */
    String getTitle();

    /**
     * Sets title of the mailbox.
     */
    void setTitle(String title);

    /**
     * Returns description of the mailbox.
     */
    String getDescription();

    /**
     * Sets description of the mailbox.
     */
    void setDescription(String description);

    /**
     * Returns type of the mailbox.
     */
    String getType();

    /**
     * Sets type of the mailbox.
     */
    void setType(MailboxConstants.type type);

    /**
     * Returns owner of the mailbox.
     */
    String getOwner();

    /**
     * Sets owner of the mailbox.
     */
    void setOwner(String owner);

    /**
     * Gets the list of member groups of this mailbox.
     */
    List<String> getGroups();

    /**
     * Sets the list of member groups for this mailbox.
     */
    void setGroups(List<String> groups);

    /**
     * Persist the Mailbox
     *
     * @param session
     */
    void save(CoreSession session);

    /**
     * Returns the parentId of this mailbox
     */
    String getParentId(CoreSession session);

    /**
     * Returns the children ids of this mailbox
     */
    List<String> getChildrenIds(CoreSession session);

    /**
     * @return list of all users and groups.
     */
    List<String> getAllUsersAndGroups();
}
