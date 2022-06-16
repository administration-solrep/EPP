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

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Service storing and querying activities.
 * <p>
 * It also uses contributed {@link ActivityStreamFilter}s to store and filter activities for specific use cases.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public interface ActivityStreamService {

    /**
     * To be used as {@code filterId}
     */
    String ALL_ACTIVITIES = "allActivities";

    Activity getActivity(Serializable activityId);

    ActivitiesList getActivities(Collection<Serializable> activityIds);

    /**
     * Add and store a new {@code Activity}.
     */
    Activity addActivity(Activity activity);

    /**
     * Remove the given {@code activities}.
     */
    void removeActivities(Collection<Activity> activities);

    /**
     * Returns the list of activities filtered by the given parameters using the {@code ActivityStreamFilter} referenced
     * by {@code filterId}.
     *
     * @param filterId the id of the {@code ActivityStreamFilter} to use.
     * @param parameters this query parameters.
     * @param offset the offset (starting at 0) into the list of activities.
     * @param limit the maximum number of activities to retrieve, or 0 for all of them.
     * @throws NuxeoException if there is no {@code ActivityStreamFilter} matching the given {@code filterId}.
     */
    ActivitiesList query(String filterId, Map<String, Serializable> parameters, long offset, long limit);

    /**
     * Returns the list of activities filtered by the given parameters using the {@code ActivityStreamFilter} referenced
     * by {@code filterId}.
     *
     * @param filterId the id of the {@code ActivityStreamFilter} to use.
     * @param parameters this query parameters.
     * @throws NuxeoException if there is no {@code ActivityStreamFilter} matching the given {@code filterId}.
     */
    ActivitiesList query(String filterId, Map<String, Serializable> parameters);

    /**
     * Computes an {@link ActivityMessage} from the given {@code activity} and {@code locale}.
     */
    ActivityMessage toActivityMessage(Activity activity, Locale locale);

    /**
     * Computes an {@link ActivityMessage} from the given {@code activity}, {@code locale} and use .
     */
    ActivityMessage toActivityMessage(Activity activity, Locale locale, String activityLinkBuilderName);

    /**
     * Computes an {@link ActivityReplyMessage} from the given {@code activityReply} and {@code locale}.
     *
     * @since 5.6
     */
    ActivityReplyMessage toActivityReplyMessage(ActivityReply activityReply, Locale locale);

    /**
     * Computes an {@link ActivityReplyMessage} from the given {@code activityReply} and {@code locale}.
     *
     * @since 5.6
     */
    ActivityReplyMessage toActivityReplyMessage(ActivityReply activityReply, Locale locale,
            String activityLinkBuilderName);

    /**
     * Returns the {@link ActivityStream} with the given {@code name}, {@code null} if it does not exist.
     */
    ActivityStream getActivityStream(String name);

    /**
     * Returns the {@link ActivityLinkBuilder} with the given {@code name}.
     * <p>
     * If {@code name} is {@code null}, or if the {@link ActivityLinkBuilder} does not exist, fallback on the default
     * one if any.
     */
    ActivityLinkBuilder getActivityLinkBuilder(String name);

    /**
     * Add an {@link ActivityReply} to the {@link Activity} referenced by the {@code activityId}.
     *
     * @return the updated {@code activityReply}
     * @since 5.6
     */
    ActivityReply addActivityReply(Serializable activityId, ActivityReply activityReply);

    /**
     * Remove an {@link ActivityReply} from the {@link Activity} referenced by the {@code activityId}.
     *
     * @return the removed {@link ActivityReply} if any, {@code null} otherwise
     * @since 5.6
     */
    ActivityReply removeActivityReply(Serializable activityId, String activityReplyId);

}
