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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.utils;

/**
 * Generate session IDs.
 * <p>
 * Session IDs are long values that must be unique on the same JVM. Each call of the {@link SIDGenerator#next()} method
 * returns an unique ID (unique relative to the current running JVM).
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public final class SIDGenerator {

    private static int count = 0;

    private static final int COUNT_OFFSET = 32;

    private SIDGenerator() {
    }

    /**
     * The long unique id is generated as follow:
     * <p>
     * On the first 32 bits we put an integer value incremented at each call and that is reset to 0 when the it reaches
     * the max integer range.
     * <p>
     * On the last 32 bits the most significant part of the current timestamp in milliseconds.
     *
     * @return the next unique id in this JVM
     */
    public static synchronized long next() {
        if (count == Integer.MAX_VALUE) {
            count = 0;
        }
        long ms = System.currentTimeMillis();
        long id = (int) ms;
        id = Long.rotateLeft(id, COUNT_OFFSET);
        return id + count++;
    }

}
