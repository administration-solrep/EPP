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
 * $Id: PermissionUIItemComparator.java 28304 2007-12-21 12:13:32Z ogrisel $
 */
package org.nuxeo.ecm.core.security;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author <a href="mailto:ogrisel@nuxeo.com">Olivier Grisel</a>
 */
public class PermissionUIItemComparator implements
        Comparator<PermissionUIItemDescriptor>, Serializable {

    private static final long serialVersionUID = 6468292882222351585L;

    @Override
    public int compare(PermissionUIItemDescriptor pid1,
            PermissionUIItemDescriptor pid2) {
        int diff = pid2.getOrder() - pid1.getOrder();
        if (diff == 0) {
            return 0;
        } else if (diff > 0) { // ascending order
            return -1;
        } else {
            return 1;
        }
    }

}
