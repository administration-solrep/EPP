/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.ecm.core.storage.sql.management;

import org.nuxeo.ecm.core.blob.binary.BinaryManagerStatus;

/**
 * @author Florent Guillaume
 */
public interface SQLRepositoryStatusMBean {

    /**
     * Lists the opened sessions.
     */
    String listActiveSessions();

    /**
     * Lists the remote opened sessions
     */
    String listRemoteSessions();

    /**
     * Return the opened sessions count
     */
    int getActiveSessionsCount();

    /**
     * Clears the caches.
     */
    String clearCaches();

    /**
     * Evaluate caches size
     *
     * @since 5.7.2
     */
    long getCachesSize();

    /**
     * GC the unused binaries.
     *
     * @param delete if {@code false} don't actually delete the GCed binaries (but still return statistics about them),
     *            if {@code true} delete them
     * @return a status about the number of GCed binaries
     */
    BinaryManagerStatus gcBinaries(boolean delete);

    /**
     * Is a GC of the binaries in progress?
     * <p>
     * It's only useful to call this from a separate thread from the one that called {@link #gcBinaries}.
     *
     * @return {@code true} if a GC of the binaries is in progress
     */
    boolean isBinariesGCInProgress();

}
