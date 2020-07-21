/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *
 * $Id: WSException.java 23153 2007-07-30 14:07:29Z janguenot $
 */

package org.nuxeo.ecm.platform.api.ws;

/**
 * Web Service exception.
 *
 * @author <a href="mailto:ja@nuxeo.com">Julien Anguenot</a>
 */
public class WSException extends Exception {

    private static final long serialVersionUID = -961763618434457798L;

    public WSException() {
    }

    public WSException(String message) {
        super(message);
    }

    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSException(Throwable cause) {
        super(cause);
    }

}
