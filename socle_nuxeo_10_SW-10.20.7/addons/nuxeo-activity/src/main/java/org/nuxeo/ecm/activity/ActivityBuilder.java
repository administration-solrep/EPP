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

import java.util.Date;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public final class ActivityBuilder {

    private String actor;

    private String displayActor;

    private String verb;

    private String object;

    private String displayObject;

    private String target;

    private String displayTarget;

    private String context;

    private Date publishedDate;

    public ActivityBuilder() {
        // Empty constructor
    }

    public ActivityBuilder(Activity fromActivity) {
        actor(fromActivity.getActor());
        context(fromActivity.getContext());
        displayActor(fromActivity.getDisplayActor());
        displayTarget(fromActivity.getDisplayTarget());
        displayObject(fromActivity.getDisplayObject());
        target(fromActivity.getTarget());
        verb(fromActivity.getVerb());
        object(fromActivity.getObject());
    }

    public ActivityBuilder actor(String actor) {
        this.actor = actor;
        return this;
    }

    public ActivityBuilder displayActor(String displayActor) {
        this.displayActor = displayActor;
        return this;
    }

    public ActivityBuilder verb(String verb) {
        this.verb = verb;
        return this;
    }

    public ActivityBuilder object(String object) {
        this.object = object;
        return this;
    }

    public ActivityBuilder displayObject(String displayObject) {
        this.displayObject = displayObject;
        return this;
    }

    public ActivityBuilder target(String target) {
        this.target = target;
        return this;
    }

    public ActivityBuilder displayTarget(String displayTarget) {
        this.displayTarget = displayTarget;
        return this;
    }

    public ActivityBuilder context(String context) {
        this.context = context;
        return this;
    }

    public ActivityBuilder publishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
        return this;
    }

    public Activity build() {
        Activity activity = new ActivityImpl();
        activity.setActor(actor);
        activity.setDisplayActor(displayActor);
        activity.setObject(object);
        activity.setDisplayObject(displayObject);
        activity.setTarget(target);
        activity.setDisplayTarget(displayTarget);
        activity.setContext(context);
        activity.setVerb(verb);
        Date date = publishedDate != null ? publishedDate : new Date();
        activity.setPublishedDate(date);
        activity.setLastUpdatedDate(date);
        return activity;
    }

}
