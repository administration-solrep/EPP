/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.core.listener;

import org.nuxeo.ecm.core.api.event.CoreEvent;
import org.nuxeo.ecm.core.api.operation.Operation;

/**
 * This listener is notified before a transaction is committed.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface PreCommitListener extends TransactedListener {

    /**
     * Current transaction is about to commit.
     * <p>
     * This method should be used by listeners using the CoreEvent model
     * and ignored by the one using Operation events.
     *
     * @param events all core events collected in current transaction.
     */
    void aboutToCommit(CoreEvent[] events);

    /**
     * Current transaction is about to commit.
     * <p>
     * This method should be used by listeners using the Operation events
     * and ignored by the one using CoreEvent events.
     *
     * @param events all operation events collected in current transaction
     */
    void aboutToCommit(Operation<?>[] events);

}
