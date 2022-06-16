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
 *     vpasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.ecm.automation.core.exception;

/**
 * @since 5.7.3
 */
public class CatchChainException {

    protected final String chainId;

    protected final Integer priority;

    protected final String filterId;

    protected final Boolean rollBack;

    public CatchChainException() {
        chainId = "";
        priority = 0;
        rollBack = true;
        filterId = "";
    }

    public CatchChainException(String chainId, Integer priority, Boolean rollBack, String filterId) {
        this.chainId = chainId;
        this.priority = priority;
        this.rollBack = rollBack;
        this.filterId = filterId;
    }

    public String getChainId() {
        return chainId;
    }

    public Integer getPriority() {
        return priority;
    }

    public Boolean getRollBack() {
        return rollBack;
    }

    public String getFilterId() {
        return filterId;
    }

    public Boolean hasFilter() {
        return !filterId.isEmpty();
    }

}
