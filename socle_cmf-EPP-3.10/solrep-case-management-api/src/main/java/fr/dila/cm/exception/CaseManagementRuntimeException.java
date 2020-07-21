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
package fr.dila.cm.exception;

/**
 * Runtime Distribution Exception
 *
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class CaseManagementRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CaseManagementRuntimeException() {
    }

    public CaseManagementRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaseManagementRuntimeException(String message) {
        super(message);
    }

    public CaseManagementRuntimeException(Throwable cause) {
        super(cause);
    }

}
