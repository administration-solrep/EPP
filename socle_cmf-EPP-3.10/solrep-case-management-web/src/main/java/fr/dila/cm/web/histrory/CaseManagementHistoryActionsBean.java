/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 *
 * $Id$
 */

// FIXME: typoi package name
package fr.dila.cm.web.histrory;

import static org.jboss.seam.ScopeType.EVENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.AuditException;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.web.listener.ContentHistoryActions;
import org.nuxeo.ecm.platform.audit.web.listener.ejb.LinkedDocument;

import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.web.invalidations.CaseManagementContextBound;
import fr.dila.cm.web.invalidations.CaseManagementContextBoundInstance;

/**
 * Retrieves log entries for current mailbox
 *
 * @author Anahide Tchertchian
 */
@Name("cmHistoryActions")
@Scope(ScopeType.CONVERSATION)
@CaseManagementContextBound
public class CaseManagementHistoryActionsBean extends
CaseManagementContextBoundInstance {

    private static final long serialVersionUID = 1L;

    // FIXME: find a better way to handle this...

    protected List<? extends LogEntry> logEntries;

    protected Map<Long, String> logEntriesComments;

    protected Map<Long, LinkedDocument> logEntriesLinkedDocs;

    protected List<? extends LogEntry> distributionLogEntries;

    protected Map<Long, String> distributionLogEntriesComments;

    protected Map<Long, LinkedDocument> distributionLogEntriesLinkedDocs;

    @In(create = true)
    protected transient ContentHistoryActions contentHistoryActions;

    protected void resetCurrentEmailCache(DocumentModel cachedEmail,
            DocumentModel newEmail) throws ClientException {
        logEntries = null;
        logEntriesComments = null;
        logEntriesLinkedDocs = null;
        distributionLogEntries = null;
        distributionLogEntriesComments = null;
        distributionLogEntriesLinkedDocs = null;
    }

    @Factory(value = "caseItemLogEntriesComments", scope = EVENT)
    public Map<Long, String> computeLogEntriesComments() throws AuditException {
        if (logEntriesComments == null) {
            postProcessComments(logEntries);
        }
        return logEntriesComments;
    }

    @Factory(value = "caseItemDistributionLogEntriesComments", scope = EVENT)
    public Map<Long, String> computeDistributionLogEntriesComments()
    throws AuditException {
        if (distributionLogEntriesComments == null) {
            postDistributionProcessComments(distributionLogEntries);
        }
        return distributionLogEntriesComments;
    }

    @Factory(value = "caseItemLogEntriesLinkedDocs", scope = EVENT)
    public Map<Long, LinkedDocument> computeLogEntrieslinkedDocs()
    throws AuditException {
        if (logEntriesLinkedDocs == null) {
            postProcessComments(logEntries);
        }
        return logEntriesLinkedDocs;
    }

    @Factory(value = "caseItemDistibutionEntriesLogLinkedDocs", scope = EVENT)
    public Map<Long, LinkedDocument> computeLogDistributionEntrieslinkedDocs()
    throws AuditException {
        if (distributionLogEntriesLinkedDocs == null) {
            postDistributionProcessComments(distributionLogEntries);
        }
        return logEntriesLinkedDocs;
    }

    protected void postProcessComments(List<? extends LogEntry> logEntries)
    throws AuditException {
        logEntriesComments = new HashMap<Long, String>();
        logEntriesLinkedDocs = new HashMap<Long, LinkedDocument>();

        if (logEntries == null) {
            return;
        }

        for (LogEntry entry : logEntries) {
            logEntriesComments.put(entry.getId(),
                    contentHistoryActions.getLogComment(entry));
            LinkedDocument linkedDoc = contentHistoryActions.getLogLinkedDocument(entry);
            if (linkedDoc != null) {
                logEntriesLinkedDocs.put(entry.getId(), linkedDoc);
            }
        }
    }

    protected void postDistributionProcessComments(
            List<? extends LogEntry> logEntries) throws AuditException {
        distributionLogEntriesComments = new HashMap<Long, String>();
        distributionLogEntriesLinkedDocs = new HashMap<Long, LinkedDocument>();

        if (logEntries == null) {
            return;
        }

        for (LogEntry entry : logEntries) {
            // Compute entry comment and place each recipient on a new line, in
            // order to have a proper layout in the 'Diffusions' tab.
            String entryComment = contentHistoryActions.getLogComment(entry);
            if (entryComment != null) {
                entryComment = entryComment.replace(":", ":\n").replace(",",
                ",\n");
            }
            distributionLogEntriesComments.put(entry.getId(), entryComment);
            LinkedDocument linkedDoc = contentHistoryActions.getLogLinkedDocument(entry);
            if (linkedDoc != null) {
                distributionLogEntriesLinkedDocs.put(entry.getId(), linkedDoc);
            }
        }
    }

    // FIXME: optimize....
    protected List<LogEntry> distributionPostFilter(
            List<? extends LogEntry> logEntries) throws AuditException {
        List<LogEntry> distLogEntries = new ArrayList<LogEntry>();

        if (logEntries == null) {
            return null;
        }

        for (LogEntry entry : logEntries) {
            if (CaseManagementEventConstants.DISTRIBUTION_CATEGORY.equals(entry.getCategory())) {
                distLogEntries.add(entry);
            }
        }

        return distLogEntries;
    }

    // FIXME: optimize....
    protected List<LogEntry> everyThingElseThanDistributionPostFilter(
            List<? extends LogEntry> logEntries) throws AuditException {
        List<LogEntry> otherLogEntries = new ArrayList<LogEntry>();

        if (logEntries == null) {
            return null;
        }

        for (LogEntry entry : logEntries) {
            if (!CaseManagementEventConstants.DISTRIBUTION_CATEGORY.equals(entry.getCategory())) {
                otherLogEntries.add(entry);
            }
        }

        return otherLogEntries;
    }

}
