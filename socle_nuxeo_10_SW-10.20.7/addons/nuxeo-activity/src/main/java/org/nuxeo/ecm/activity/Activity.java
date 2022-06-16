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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Representation of an Activity.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public interface Activity {

    Serializable getId();

    String getActor();

    void setActor(String actor);

    String getDisplayActor();

    void setDisplayActor(String displayActor);

    String getVerb();

    void setVerb(String verb);

    String getObject();

    void setObject(String object);

    String getDisplayObject();

    void setDisplayObject(String displayObject);

    String getTarget();

    void setTarget(String target);

    String getDisplayTarget();

    void setDisplayTarget(String displayTarget);

    /**
     * Returns the context of this {@code Activity}.
     *
     * @since 5.6
     */
    String getContext();

    /**
     * Sets the context of this {@code Activity}.
     *
     * @since 5.6
     */
    void setContext(String context);

    Date getPublishedDate();

    void setPublishedDate(Date publishedDate);

    /**
     * Returns the last updated date of this {@code Activity}.
     *
     * @since 5.6
     */
    Date getLastUpdatedDate();

    /**
     * Sets the last updated date of this {@code Activity}.
     *
     * @since 5.6
     */
    void setLastUpdatedDate(Date lastUpdatedDate);

    /**
     * Returns the replies of this {@code Activity}.
     * <p>
     * The replies are stored as a JSON string.
     *
     * @since 5.6
     */
    String getReplies();

    /**
     * Sets the replies of this {@code Activity}.
     *
     * @since 5.6
     */
    void setReplies(String replies);

    /**
     * Returns the list of {@link ActivityReply} of this {@code Activity}.
     *
     * @since 5.6
     */
    List<ActivityReply> getActivityReplies();

    /**
     * Sets the replies of this {@code Activity}.
     *
     * @since 5.6
     */
    void setActivityReplies(List<ActivityReply> activityReplies);

    Map<String, String> toMap();

}
