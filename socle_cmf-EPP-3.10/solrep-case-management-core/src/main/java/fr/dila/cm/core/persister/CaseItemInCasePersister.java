/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */
package fr.dila.cm.core.persister;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.cm.cases.Case;
import fr.dila.cm.service.CaseManagementPersister;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class CaseItemInCasePersister extends CaseManagementAbstractPersister
        implements CaseManagementPersister {

    @Override
    public String getParentDocumentPathForCaseItem(CoreSession session,
            Case kase) {
        try {
            return session.getDocument(kase.getDocument().getRef()).getPathAsString();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
