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
package fr.dila.ecm.platform.routing.api;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.ecm.platform.routing.api.helper.ActionableValidator;

/**
 * An actionable object is an object that can be validated or refused.
 *
 * @see ActionableValidator
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public interface ActionableObject {

    /**
     * The operation chain id if the action is refused.
     *
     * @return
     */
    String getRefuseOperationChainId();

    /**
     * The operation chain id if the action is validated.
     *
     * @return
     */
    String getValidateOperationChainId();

    /**
     * The step that represent the action.
     *
     * @param session
     * @return
     */
    DocumentRouteStep getDocumentRouteStep(CoreSession session);

    /**
     * The documents processed by the action.
     */
    DocumentModelList getAttachedDocuments(CoreSession session);

}
