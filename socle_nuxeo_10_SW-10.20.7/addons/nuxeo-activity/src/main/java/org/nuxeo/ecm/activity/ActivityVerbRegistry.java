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

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.runtime.model.ContributionFragmentRegistry;

/**
 * Registry for activity verbs, handling merge of registered {@link org.nuxeo.ecm.activity.ActivityVerb} elements.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
public class ActivityVerbRegistry extends ContributionFragmentRegistry<ActivityVerb> {

    protected Map<String, ActivityVerb> activityVerbs = new HashMap<String, ActivityVerb>();

    public ActivityVerb get(String name) {
        return activityVerbs.get(name);
    }

    @Override
    public String getContributionId(ActivityVerb contrib) {
        return contrib.getVerb();
    }

    @Override
    public void contributionUpdated(String id, ActivityVerb contrib, ActivityVerb newOrigContrib) {
        activityVerbs.put(id, contrib);
    }

    @Override
    public void contributionRemoved(String id, ActivityVerb origContrib) {
        activityVerbs.remove(id);
    }

    @Override
    public ActivityVerb clone(ActivityVerb orig) {
        return orig.clone();
    }

    @Override
    public void merge(ActivityVerb src, ActivityVerb dst) {
        String labelKey = src.getLabelKey();
        if (labelKey != null) {
            dst.setLabelKey(labelKey);
        }
        String icon = src.getIcon();
        if (icon != null) {
            dst.setIcon(icon);
        }
    }
}
