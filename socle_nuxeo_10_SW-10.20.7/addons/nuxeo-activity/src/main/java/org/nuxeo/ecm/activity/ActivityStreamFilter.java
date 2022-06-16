/*
 * (C) Copyright 2011-2016 Nuxeo SA (http://nuxeo.com/) and others.
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

import java.io.Serializable;
import java.util.Map;

/**
 * Filter called by the {@code ActivityStreamService} to store and filter activities for specific use cases.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public interface ActivityStreamFilter {

    /**
     * Returns the id of this {@code ActivityStreamFilter}.
     */
    String getId();

    /**
     * Returns {@code true} if this {@code ActivityStreamFilter} is interested in the given {@code activity},
     * {@code false} otherwise.
     */
    boolean isInterestedIn(Activity activity);

    /**
     * Called by the {@code ActivityStreamService} when a new {@code Activity} is stored.
     * <p>
     * The given {@code activity} must not be modified.
     */
    void handleNewActivity(ActivityStreamService activityStreamService, Activity activity);

    /**
     * Called by the {@code ActivityStreamService} before removing the given {@code activities}.
     *
     * @since 5.6
     */
    void handleRemovedActivities(ActivityStreamService activityStreamService, ActivitiesList activities);

    /**
     * Called by the {@code ActivityStreamService} before removing the given {@code activityReply}.
     *
     * @since 5.6
     */
    void handleRemovedActivityReply(ActivityStreamService activityStreamService, Activity activity,
            ActivityReply activityReply);

    /**
     * Returns the list of activities filtered by the given parameters.
     *
     * @param activityStreamService the main {@code ActivityStreamService}
     * @param parameters this query parameters.
     * @param offset the offset (starting at 0) into the list of activities.
     * @param limit the maximum number of activities to retrieve, or 0 for all of them.
     */
    ActivitiesList query(ActivityStreamService activityStreamService, Map<String, Serializable> parameters, long offset,
            long limit);

}
