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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.api.Framework;

/**
 * Default implementation of {@link ActivitiesList}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class ActivitiesListImpl extends ArrayList<Activity> implements ActivitiesList {

    private static final long serialVersionUID = 1L;

    public ActivitiesListImpl() {
        super();
    }

    public ActivitiesListImpl(Collection<? extends Activity> c) {
        super(c);
    }

    @Override
    public ActivitiesList filterActivities(CoreSession session) {
        ActivitiesList filteredActivities = new ActivitiesListImpl(this);

        Map<String, List<Activity>> activitiesByDocument = getActivitiesByDocument();

        List<String> authorizedDocuments = filterAuthorizedDocuments(activitiesByDocument.keySet(), session);
        // remove all activities the user has access to
        for (String authorizedDocument : authorizedDocuments) {
            activitiesByDocument.remove(authorizedDocument);
        }

        // extract all unauthorized activities
        List<Activity> unauthorizedActivities = new ArrayList<>();
        for (List<Activity> activities : activitiesByDocument.values()) {
            unauthorizedActivities.addAll(activities);
        }

        // remove all unauthorized activities
        filteredActivities.removeAll(unauthorizedActivities);
        return filteredActivities;
    }

    protected Map<String, List<Activity>> getActivitiesByDocument() {
        Map<String, List<Activity>> activitiesByDocuments = new HashMap<>();
        for (Activity activity : this) {
            List<String> relatedDocuments = getRelatedDocuments(activity);
            for (String doc : relatedDocuments) {
                List<Activity> value = activitiesByDocuments.get(doc);
                if (value == null) {
                    value = new ArrayList<>();
                    activitiesByDocuments.put(doc, value);
                }
                value.add(activity);
            }
        }
        return activitiesByDocuments;
    }

    protected List<String> getRelatedDocuments(Activity activity) {
        List<String> relatedDocuments = new ArrayList<>();

        String activityObject = activity.getActor();
        if (activityObject != null && ActivityHelper.isDocument(activityObject)) {
            relatedDocuments.add(ActivityHelper.getDocumentId(activityObject));
        }
        activityObject = activity.getObject();
        if (activityObject != null && ActivityHelper.isDocument(activityObject)) {
            relatedDocuments.add(ActivityHelper.getDocumentId(activityObject));
        }
        activityObject = activity.getTarget();
        if (activityObject != null && ActivityHelper.isDocument(activityObject)) {
            relatedDocuments.add(ActivityHelper.getDocumentId(activityObject));
        }

        return relatedDocuments;
    }

    protected List<String> filterAuthorizedDocuments(Set<String> allDocuments, CoreSession session) {
        String idsParam = "('" + String.join("', '", allDocuments) + "')";
        String query = String.format("SELECT ecm:uuid FROM Document WHERE ecm:uuid IN %s", idsParam);
        IterableQueryResult res = session.queryAndFetch(query, "NXQL");

        try {
            List<String> authorizedDocuments = new ArrayList<>();
            for (Map<String, Serializable> map : res) {
                authorizedDocuments.add((String) map.get("ecm:uuid"));
            }
            return authorizedDocuments;
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }

    @Override
    public List<ActivityMessage> toActivityMessages(Locale locale) {
        ActivityStreamService activityStreamService = Framework.getService(ActivityStreamService.class);
        List<ActivityMessage> messages = new ArrayList<>();
        for (Activity activity : this) {
            messages.add(activityStreamService.toActivityMessage(activity, locale));
        }
        return messages;
    }

    @Override
    public List<ActivityMessage> toActivityMessages(Locale locale, String activityLinkBuilderName) {
        ActivityStreamService activityStreamService = Framework.getService(ActivityStreamService.class);
        List<ActivityMessage> messages = new ArrayList<>();
        for (Activity activity : this) {
            messages.add(activityStreamService.toActivityMessage(activity, locale, activityLinkBuilderName));
        }
        return messages;
    }

    @Override
    public List<Serializable> toActivityIds() {
        List<Serializable> activityIds = new ArrayList<>();
        for (Activity activity : this) {
            activityIds.add(activity.getId());
        }
        return activityIds;
    }

}
