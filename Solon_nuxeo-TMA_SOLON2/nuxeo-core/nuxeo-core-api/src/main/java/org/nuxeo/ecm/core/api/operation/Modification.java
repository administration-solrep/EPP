/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.operation;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Modification implements Serializable {

    private static final long serialVersionUID = 9222943443571217778L;

    public static final int ADD_CHILD = 1;
    public static final int REMOVE_CHILD = 2;
    public static final int ORDER_CHILD = 4;
    public static final int CONTAINER_MODIFICATION = ADD_CHILD | REMOVE_CHILD | ORDER_CHILD;

    public static final int CREATE = 8;
    public static final int REMOVE = 16;
    public static final int MOVE = 32;
    public static final int EXISTENCE_MODIFICATION = CREATE | REMOVE | MOVE;

    public static final int CONTENT = 64;
    public static final int SECURITY = 128;
    public static final int STATE = 256;
    public static final int UPDATE_MODIFICATION = CONTENT | SECURITY | STATE;

    public int type;
    public final DocumentRef ref;

    public Modification(DocumentRef ref, int type) {
        this.ref = ref;
        this.type = type;
    }

    public final boolean isUpdateModification() {
        return (type & UPDATE_MODIFICATION) != 0;
    }

    public final boolean isContainerModification() {
        return (type & CONTAINER_MODIFICATION) != 0;
    }

    public final boolean isExistenceModification() {
        return (type & EXISTENCE_MODIFICATION) != 0;
    }

    public final boolean isAddChild() {
        return (type & ADD_CHILD) != 0;
    }

    public final boolean isRemoveChild() {
        return (type & REMOVE_CHILD) != 0;
    }

    public final boolean isOrderChild() {
        return (type & ORDER_CHILD) != 0;
    }

    public final boolean isCreate() {
        return (type & CREATE) != 0;
    }

    public final boolean isRemove() {
        return (type & REMOVE) != 0;
    }

    public final boolean isContentUpdate() {
        return (type & CONTENT) != 0;
    }

    public final boolean isStateUpdate() {
        return (type & STATE) != 0;
    }

    public final boolean isSecurityUpdate() {
        return (type & SECURITY) != 0;
    }

    @Override
    public String toString() {
        return ref + " [" + type + ']';
    }

}
