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
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.nuxeo.ecm.activity;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * A list of Activities with useful methods to filter it or transform it.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public interface ActivitiesList extends List<Activity> {

    /**
     * Returns a filtered {@code ActivitiesList} based on the given {@code session}.
     * <p>
     * All the activities related to documents the user has no read access will be filter out.
     */
    ActivitiesList filterActivities(CoreSession session);

    /**
     * Transforms this {@code ActivitiesList} into a list of {@code ActivityMessage}, internationalized with the given
     * {@code locale}.
     */
    List<ActivityMessage> toActivityMessages(Locale locale);

    /**
     * Transforms this {@code ActivitiesList} into a list of {@code ActivityMessage}, internationalized with the given
     * {@code locale}.
     * <p>
     * Use the {@link ActivityLinkBuilder} of name {@code activityLinkBuilderName} to generate the links.
     *
     * @since 5.6
     */
    List<ActivityMessage> toActivityMessages(Locale locale, String activityLinkBuilderName);

    /**
     * Transforms this {@code ActivitiesList} into a list of Activity ids.
     *
     * @since 5.6
     */
    List<Serializable> toActivityIds();

}
