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
 */

package org.nuxeo.ecm.core.management.api;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Representation of the Administrative Status of a service or server
 *
 * @author tiry
 */
public class AdministrativeStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ACTIVE = "active";

    public static final String PASSIVE = "passive";

    protected final String state;

    protected final String message;

    protected final Calendar modificationDate;

    protected final String userLogin;

    protected final String instanceIdentifier;

    protected final String serviceIdentifier;

    protected String label;

    protected String description;

    public AdministrativeStatus(String state, String message,
            Calendar modificationDate, String userLogin,
            String instanceIdentifier, String serviceIdentifier) {
        this.state = state;
        this.message = message;
        this.modificationDate = modificationDate;
        this.userLogin = userLogin;
        this.instanceIdentifier = instanceIdentifier;
        this.serviceIdentifier = serviceIdentifier;
    }

    public void setLabelAndDescription(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getInstanceIdentifier() {
        return instanceIdentifier;
    }

    public String getServiceIdentifier() {
        return serviceIdentifier;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public Calendar getModificationDate() {
        return modificationDate;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public boolean isActive() {
        return state.equals(ACTIVE);
    }

    public boolean isPassive() {
        return state.equals(PASSIVE);
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("administrativeStatus(%s,%s)", instanceIdentifier,
                state);
    }

}
