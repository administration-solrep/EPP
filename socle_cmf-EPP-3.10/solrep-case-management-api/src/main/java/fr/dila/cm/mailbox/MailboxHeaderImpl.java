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
 *     Laurent Doguin
 *
 * $Id$
 */

package fr.dila.cm.mailbox;

/**
 * MailboxHeader implementation
 *
 * @author Laurent Doguin
 */
public class MailboxHeaderImpl implements MailboxHeader {

    private static final long serialVersionUID = 1L;

    protected final String title;

    protected final String id;

    protected final String type;

    public MailboxHeaderImpl(String id, String title, String type) {
        this.title = title;
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int compareTo(MailboxHeader other) {
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

}
