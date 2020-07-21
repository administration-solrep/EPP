/* 
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id: AuditQueryException.java 20577 2007-06-16 09:26:07Z sfermigier $
 */

package org.nuxeo.ecm.platform.audit.api.query;

import org.nuxeo.ecm.platform.audit.api.AuditException;

/**
 * NXAudit-Query related exception.
 *
 * @author <a href="mailto:ja@nuxeo.com">Julien Anguenot</a>
 */
public class AuditQueryException extends AuditException {

    private static final long serialVersionUID = 1L;

    public AuditQueryException() {
    }

    public AuditQueryException(String message) {
        super(message);
    }

    public AuditQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuditQueryException(Throwable cause) {
        super(cause);
    }

}
