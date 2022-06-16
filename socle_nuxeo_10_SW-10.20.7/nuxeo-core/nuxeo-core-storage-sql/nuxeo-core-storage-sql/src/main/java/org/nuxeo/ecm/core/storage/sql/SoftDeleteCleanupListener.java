/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLRepositoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * Sync listener that calls the soft delete cleanup method of the repositories. Designed to be called periodically.
 *
 * @since 5.7
 */
public class SoftDeleteCleanupListener implements EventListener {

    private static final Log log = LogFactory.getLog(SoftDeleteCleanupListener.class);

    public static final int DEFAULT_MAX = 1000;

    /**
     * Property for the maximum number of documents to delete in one call. Zero means all the documents. Default is
     * {@value #DEFAULT_MAX}.
     */
    public static final String DEFAULT_MAX_PROP = "org.nuxeo.vcs.softdelete.cleanup.max";

    public static final int DEFAULT_DELAY = 5 * 60; // 5 min

    /**
     * Property for the minimum delay (in seconds) since when a document must have been soft-deleted before it can be
     * hard-deleted. Zero means no delay. Default is {@value #DEFAULT_DELAY}.
     */
    public static final String DEFAULT_DELAY_PROP = "org.nuxeo.vcs.softdelete.cleanup.age";

    /**
     * Gets the maximum number of documents to delete in one call. Zero means all the documents.
     */
    protected int getMax() {
        String max = Framework.getProperty(DEFAULT_MAX_PROP);
        if (max == null) {
            return DEFAULT_MAX;
        }
        try {
            return Integer.parseInt(max);
        } catch (NumberFormatException e) {
            log.error("Invalid property " + DEFAULT_MAX_PROP, e);
            return DEFAULT_MAX;
        }
    }

    /**
     * Gets the minimum delay (in seconds) since when a document must have been soft-deleted before it can be
     * hard-deleted. Zero means no delay.
     */
    protected int getDelaySeconds() {
        String delay = Framework.getProperty(DEFAULT_DELAY_PROP);
        if (delay == null) {
            return DEFAULT_DELAY;
        }
        try {
            return Integer.parseInt(delay);
        } catch (NumberFormatException e) {
            log.error("Invalid property " + DEFAULT_DELAY_PROP, e);
            return DEFAULT_DELAY;
        }
    }

    @Override
    public void handleEvent(Event event) {
        RepositoryService repositoryService = Framework.getService(RepositoryService.class);
        if (repositoryService == null) {
            // RepositoryService failed to start, no need to go further
            return;
        }
        int max = getMax();
        int delay = getDelaySeconds();
        Calendar beforeTime;
        if (delay <= 0) {
            beforeTime = null;
        } else {
            beforeTime = Calendar.getInstance();
            beforeTime.add(Calendar.SECOND, -delay);
        }
        SQLRepositoryService sqlRepositoryService = Framework.getService(SQLRepositoryService.class);
        for (RepositoryManagement repoMgmt : sqlRepositoryService.getRepositories()) {
            log.debug("Calling repository soft-delete cleanup for repository: " + repoMgmt.getName() + ", max=" + max
                    + ", beforeTimeDelay=" + delay);
            int n = repoMgmt.cleanupDeletedDocuments(max, beforeTime);
            log.debug("Number of documents deleted: " + n);
        }
    }

}
