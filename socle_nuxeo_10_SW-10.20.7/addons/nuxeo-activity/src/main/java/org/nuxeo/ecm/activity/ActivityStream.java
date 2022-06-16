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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
@XObject("activityStream")
public class ActivityStream {

    @XNode("@name")
    String name;

    @XNode("verbs@append")
    boolean appendVerbs;

    @XNodeList(value = "verbs/verb", type = ArrayList.class, componentType = String.class)
    List<String> verbs;

    @XNode("relationshipKinds@append")
    boolean appendRelationshipKinds;

    @XNodeList(value = "relationshipKinds/relationshipKind", type = ArrayList.class, componentType = String.class)
    List<String> relationshipKinds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAppendVerbs() {
        return appendVerbs;
    }

    public void setAppendVerbs(boolean appendVerbs) {
        this.appendVerbs = appendVerbs;
    }

    public List<String> getVerbs() {
        if (verbs == null) {
            return Collections.emptyList();
        }
        return verbs;
    }

    public void setVerbs(List<String> verbs) {
        this.verbs = verbs;
    }

    public boolean isAppendRelationshipKinds() {
        return appendRelationshipKinds;
    }

    public void setAppendRelationshipKinds(boolean appendRelationshipKinds) {
        this.appendRelationshipKinds = appendRelationshipKinds;
    }

    public List<String> getRelationshipKinds() {
        if (relationshipKinds == null) {
            return Collections.emptyList();
        }
        return relationshipKinds;
    }

    public void setRelationshipKinds(List<String> relationshipKinds) {
        this.relationshipKinds = relationshipKinds;
    }

    @Override
    public ActivityStream clone() {
        ActivityStream clone = new ActivityStream();
        clone.setName(getName());
        clone.setAppendVerbs(isAppendVerbs());
        List<String> verbs = getVerbs();
        if (verbs != null) {
            List<String> newVerbs = new ArrayList<String>();
            for (String verb : verbs) {
                newVerbs.add(verb);
            }
            clone.setVerbs(newVerbs);
        }
        clone.setAppendRelationshipKinds(isAppendRelationshipKinds());
        List<String> relationshipKinds = getRelationshipKinds();
        if (relationshipKinds != null) {
            List<String> newRelationshipKinds = new ArrayList<String>();
            for (String kind : relationshipKinds) {
                newRelationshipKinds.add(kind);
            }
            clone.setRelationshipKinds(newRelationshipKinds);
        }
        return clone;
    }
}
