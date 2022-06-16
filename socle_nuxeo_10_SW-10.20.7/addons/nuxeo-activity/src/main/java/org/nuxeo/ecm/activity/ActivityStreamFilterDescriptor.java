/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.nuxeo.ecm.activity;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Descriptor object for registering {@link ActivityStreamFilter}s.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
@XObject("activityStreamFilter")
public class ActivityStreamFilterDescriptor {

    @XNode("@enabled")
    protected boolean enabled = true;

    @XNode("@class")
    protected Class<? extends ActivityStreamFilter> activityStreamFilterClass;

    public ActivityStreamFilterDescriptor() {
    }

    public ActivityStreamFilterDescriptor(Class<? extends ActivityStreamFilter> activityStreamFilterClass,
            boolean enabled) {
        this.activityStreamFilterClass = activityStreamFilterClass;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ActivityStreamFilter getActivityStreamFilter() {
        try {
            return activityStreamFilterClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new NuxeoException(e);
        }
    }

}
