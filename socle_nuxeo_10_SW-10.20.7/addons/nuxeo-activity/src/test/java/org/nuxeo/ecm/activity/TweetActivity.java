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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
@Entity(name = "Tweet")
@Table(name = "tweets")
public class TweetActivity {

    private Long id;

    private String seenBy;

    private Long activityId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, columnDefinition = "integer")
    public Long getInternalId() {
        return id;
    }

    public void setInternalId(Long id) {
        this.id = id;
    }

    public Serializable getId() {
        return id;
    }

    @Column(name = "activityId", columnDefinition = "integer")
    public Long getActivityId() {
        return activityId;
    }

    @Column(name = "seenBy")
    public String getSeenBy() {
        return seenBy;
    }

    public void setActivityId(Serializable activityId) {
        this.activityId = (Long) activityId;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }

    public void setId(Serializable id) {
        this.id = (Long) id;
    }

}
