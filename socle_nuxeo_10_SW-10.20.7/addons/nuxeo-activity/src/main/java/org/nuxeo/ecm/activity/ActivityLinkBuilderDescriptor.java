/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 * Descriptor object for registering {@link ActivityLinkBuilder}s.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
@XObject("activityLinkBuilder")
public class ActivityLinkBuilderDescriptor {

    @XNode("@name")
    protected String name;

    @XNode("@class")
    protected Class<? extends ActivityLinkBuilder> activityLinkBuilderClass;

    @XNode("@default")
    protected boolean isDefault = false;

    @XNode("@enabled")
    protected boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends ActivityLinkBuilder> getActivityLinkBuilderClass() {
        return activityLinkBuilderClass;
    }

    public void setActivityLinkBuilderClass(Class<? extends ActivityLinkBuilder> activityLinkBuilderClass) {
        this.activityLinkBuilderClass = activityLinkBuilderClass;
    }

    public ActivityLinkBuilder getActivityLinkBuilder() {
        try {
            return activityLinkBuilderClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new NuxeoException(e);
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public ActivityLinkBuilderDescriptor clone() {
        ActivityLinkBuilderDescriptor clone = new ActivityLinkBuilderDescriptor();
        clone.setName(name);
        clone.setActivityLinkBuilderClass(activityLinkBuilderClass);
        clone.setDefault(isDefault);
        clone.setEnabled(enabled);
        return clone;
    }

}
