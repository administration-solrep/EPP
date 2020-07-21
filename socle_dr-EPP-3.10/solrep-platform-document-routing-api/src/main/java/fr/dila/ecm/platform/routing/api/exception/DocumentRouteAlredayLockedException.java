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
 *     mcedica
 */
package fr.dila.ecm.platform.routing.api.exception;

import org.nuxeo.ecm.core.api.ClientException;

/**
*
* @author <a href="mailto:mcedica@nuxeo.com">Mariana Cedica</a>
*
*/
public class DocumentRouteAlredayLockedException extends ClientException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DocumentRouteAlredayLockedException() {

    }

    public DocumentRouteAlredayLockedException(String message) {
        super(message);
    }

    public DocumentRouteAlredayLockedException(Throwable th) {
        super(th);
    }

    public DocumentRouteAlredayLockedException(String message, Throwable th) {
        super(message, th);
    }

}
