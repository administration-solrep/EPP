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

package fr.dila.cm.mock;

import fr.dila.cm.mailbox.MailboxImpl;

/**
 * @author Anahide Tchertchian
 *
 */
public class MockMailbox extends MailboxImpl {

    private static final long serialVersionUID = 1L;

    protected final String id;

    public MockMailbox(String id) {
        super(null);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
