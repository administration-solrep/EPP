/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     <a href="mailto:grenard@nuxeo.com">Guillaume</a>
 */
package org.nuxeo.ecm.platform.sessioninspector.jsf.model;

/**
 * Might be useful to check for references stats (in case same object is referenced in several items state).
 *
 * @since 5.9.2
 */
public class ObjectStatistics {

    private String type;

    private long nbInstance;

    private long cumulatedSize;

    /**
     * @param type
     * @param nbObject
     * @param cumulatedSize
     */
    public ObjectStatistics(String type, long nbInstance, long cumulatedSize) {
        super();
        this.type = type;
        this.nbInstance = nbInstance;
        this.cumulatedSize = cumulatedSize;
    }

    /**
     * @param type
     * @param nbObject
     * @param cumulatedSize
     */
    public ObjectStatistics(String type) {
        this(type, 1, 0);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getNbInstance() {
        return nbInstance;
    }

    public void setNbInstance(long nbInstance) {
        this.nbInstance = nbInstance;
    }

    public long getCumulatedSize() {
        return cumulatedSize;
    }

    public void setCumulatedSize(long cumulatedSize) {
        this.cumulatedSize = cumulatedSize;
    }

}
