/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
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

import static org.nuxeo.ecm.activity.ActivityHelper.getUsername;
import static org.nuxeo.ecm.activity.ActivityHelper.isUser;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * Immutable object representing an Activity message.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public final class ActivityMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Serializable activityId;

    private final String actor;

    private final String displayActor;

    private final String displayActorLink;

    private final String verb;

    private final String message;

    private final Date publishedDate;

    private final String icon;

    private final List<ActivityReplyMessage> replies;

    /**
     * @since 5.6
     */
    public ActivityMessage(Serializable activityId, String actor, String displayActor, String displayActorLink,
            String verb, String message, Date publishedDate, String icon, List<ActivityReplyMessage> replies) {
        this.activityId = activityId;
        this.actor = actor;
        this.displayActor = displayActor;
        this.displayActorLink = displayActorLink;
        this.verb = verb;
        this.message = message;
        this.publishedDate = publishedDate;
        this.icon = icon;
        this.replies = replies;
    }

    public Serializable getActivityId() {
        return activityId;
    }

    /**
     * @since 5.6
     */
    public String getActor() {
        return actor;
    }

    /**
     * @since 5.6
     */
    public String getDisplayActor() {
        return displayActor;
    }

    /**
     * @since 5.6
     */
    public String getDisplayActorLink() {
        return displayActorLink;
    }

    /**
     * @since 5.6
     */
    public String getVerb() {
        return verb;
    }

    public String getMessage() {
        return message;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    /**
     * @since 5.6
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @since 5.6
     */
    public List<ActivityReplyMessage> getActivityReplyMessages() {
        return replies;
    }

    /**
     * @since 5.6
     */
    public Map<String, Object> toMap(CoreSession session, Locale locale) {
        return toMap(session, locale, null);
    }

    /**
     * @since 5.6
     */
    public Map<String, Object> toMap(CoreSession session, Locale locale, String activityLinkBuilderName) {
        ActivityLinkBuilder activityLinkBuilder = Framework.getService(ActivityStreamService.class)
                                                           .getActivityLinkBuilder(activityLinkBuilderName);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        Map<String, Object> o = new HashMap<>();
        o.put("id", getActivityId());
        o.put("actor", getActor());
        o.put("displayActor", getDisplayActor());
        o.put("displayActorLink", getDisplayActorLink());
        if (isUser(getActor())) {
            String actorUsername = getUsername(getActor());
            o.put("actorAvatarURL", activityLinkBuilder.getUserAvatarURL(session, actorUsername));
        }
        o.put("activityVerb", getVerb());
        o.put("activityMessage", getMessage());
        o.put("publishedDate", dateFormat.format(getPublishedDate()));
        o.put("icon", getIcon());
        return o;
    }

}
