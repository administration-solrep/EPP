/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.versioning;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.model.NoSuchPropertyException;

/**
 * Compatibility implementation of the versioning service in Nuxeo.
 */
public class CompatVersioningService extends StandardVersioningService {

    private static final Log log = LogFactory.getLog(CompatVersioningService.class);

    @Override
    public String getVersionLabel(DocumentModel doc) {
        try {
            return getMajor(doc) + "." + getMinor(doc);
        } catch (PropertyNotFoundException e) {
            return "";
        } catch (ClientException e) {
            log.debug("No version label", e);
            return "";
        }
    }

    @Override
    protected void setInitialVersion(Document doc) throws DocumentException {
        setVersion(doc, 1, 0);
    }

    /*
     * Create a pre-save snapshot, and re-checkout the document if there's a
     * pending save or we want to increment the version.
     */
    @Override
    public VersioningOption doPreSave(Document doc, boolean isDirty,
            VersioningOption option, String checkinComment,
            Map<String, Serializable> options) throws DocumentException {
        option = validateOption(doc, option);
        boolean increment = option != VersioningOption.NONE;
        if (increment) {
            if (doc.isCheckedOut()) {
                doc.checkIn(null, checkinComment); // auto-label
            }
        }
        if (!doc.isCheckedOut() && (isDirty || increment)) {
            doc.checkOut();
        }
        return option;
    }

    @Override
    public Document doPostSave(Document doc, VersioningOption option,
            String checkinComment, Map<String, Serializable> options)
            throws DocumentException {
        if (!doc.isCheckedOut()) {
            return null;
        }
        // option = validateOption(doc, option);
        incrementByOption(doc, option);
        followTransitionByOption(doc, option);
        return null;
    }

    @Override
    public Document doCheckIn(Document doc, VersioningOption option,
            String checkinComment) throws DocumentException {
        return doc.checkIn(null, checkinComment); // auto-label
    }

    @Override
    public void doCheckOut(Document doc) throws DocumentException {
        doc.checkOut();
        // set version number to that of the last version + inc minor
        try {
            Document last = doc.getLastVersion();
            if (last != null) {
                setVersion(doc, getMajor(last), getMinor(last) + 1);
            }
        } catch (NoSuchPropertyException e) {
            // ignore
        }
    }

}
