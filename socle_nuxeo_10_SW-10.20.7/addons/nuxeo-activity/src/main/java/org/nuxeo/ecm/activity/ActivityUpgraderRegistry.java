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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.runtime.model.ContributionFragmentRegistry;

/**
 * Registry for activity upgraders, handling merge of registered
 * {@link org.nuxeo.ecm.activity.ActivityUpgraderDescriptor} elements.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.7
 */
public class ActivityUpgraderRegistry extends ContributionFragmentRegistry<ActivityUpgraderDescriptor> {

    protected Map<String, ActivityUpgrader> activityUpgraders = new HashMap<String, ActivityUpgrader>();

    public List<ActivityUpgrader> getOrderedActivityUpgraders() {
        List<ActivityUpgrader> upgraders = new ArrayList<ActivityUpgrader>(activityUpgraders.values());
        Collections.sort(upgraders, new Comparator<ActivityUpgrader>() {
            @Override
            public int compare(ActivityUpgrader o1, ActivityUpgrader o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        return upgraders;
    }

    @Override
    public String getContributionId(ActivityUpgraderDescriptor contrib) {
        return contrib.getName();
    }

    @Override
    public void contributionUpdated(String id, ActivityUpgraderDescriptor contrib,
            ActivityUpgraderDescriptor newOrigContrib) {
        activityUpgraders.put(id, contrib.getActivityUpgrader());
    }

    @Override
    public void contributionRemoved(String id, ActivityUpgraderDescriptor origContrib) {
        activityUpgraders.remove(id);
    }

    @Override
    public ActivityUpgraderDescriptor clone(ActivityUpgraderDescriptor orig) {
        return orig.clone();
    }

    @Override
    public void merge(ActivityUpgraderDescriptor src, ActivityUpgraderDescriptor dst) {
        Class<? extends ActivityUpgrader> clazz = src.getActivityUpgraderClass();
        if (clazz != null) {
            dst.setActivityUpgraderClass(clazz);
        }
        dst.setOrder(src.getOrder());

        boolean enabled = src.isEnabled();
        if (enabled != dst.isEnabled()) {
            dst.setEnabled(enabled);
        }
    }

}
