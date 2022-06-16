/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger (troger@nuxeo.com)
 */

package org.nuxeo.ecm.activity;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Descriptor object for registering {@link org.nuxeo.ecm.activity.ActivityUpgrader}s.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.7
 */
@XObject("activityUpgrader")
public class ActivityUpgraderDescriptor {

    @XNode("@enabled")
    protected boolean enabled = true;

    @XNode("@name")
    protected String name;

    @XNode("@order")
    protected int order = 0;

    @XNode("@class")
    protected Class<? extends ActivityUpgrader> activityUpgraderClass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ActivityUpgrader getActivityUpgrader() {
        try {
            ActivityUpgrader upgrader = activityUpgraderClass.newInstance();
            upgrader.setName(name);
            upgrader.setOrder(order);
            return upgrader;
        } catch (ReflectiveOperationException e) {
            throw new NuxeoException(e);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Class<? extends ActivityUpgrader> getActivityUpgraderClass() {
        return activityUpgraderClass;
    }

    public void setActivityUpgraderClass(Class<? extends ActivityUpgrader> activityUpgraderClass) {
        this.activityUpgraderClass = activityUpgraderClass;
    }

    @Override
    public ActivityUpgraderDescriptor clone() {
        ActivityUpgraderDescriptor clone = new ActivityUpgraderDescriptor();
        clone.setName(name);
        clone.setOrder(order);
        clone.setActivityUpgraderClass(activityUpgraderClass);
        clone.setEnabled(enabled);
        return clone;
    }

}
