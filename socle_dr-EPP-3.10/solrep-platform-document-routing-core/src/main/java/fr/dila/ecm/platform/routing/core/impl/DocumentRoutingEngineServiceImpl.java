/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     arussel
 */
package fr.dila.ecm.platform.routing.core.impl;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.core.api.DocumentRoutingEngineService;

/**
 * @author arussel
 *
 */
public class DocumentRoutingEngineServiceImpl extends DefaultComponent
        implements DocumentRoutingEngineService {

    @Override
    public void start(DocumentRoute routeInstance, CoreSession session) {
        routeInstance.run(session);
    }
}
