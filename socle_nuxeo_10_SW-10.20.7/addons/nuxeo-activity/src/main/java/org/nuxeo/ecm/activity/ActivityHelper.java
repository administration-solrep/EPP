/*
 * (C) Copyright 2006-2018 Nuxeo (http://nuxeo.com/) and others.
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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PropertyException;

/**
 * Helper class to deal with activity objects.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class ActivityHelper {

    public static final String SEPARATOR = ":";

    public static final String USER_PREFIX = "user" + SEPARATOR;

    public static final String DOC_PREFIX = "doc" + SEPARATOR;

    public static final String ACTIVITY_PREFIX = "activity" + SEPARATOR;

    private ActivityHelper() {
        // helper class
    }

    public static boolean isUser(String activityObject) {
        return activityObject != null && activityObject.startsWith(USER_PREFIX);
    }

    public static boolean isDocument(String activityObject) {
        return activityObject != null && activityObject.startsWith(DOC_PREFIX);
    }

    public static boolean isActivity(String activityObject) {
        return activityObject != null && activityObject.startsWith(ACTIVITY_PREFIX);
    }

    public static String getUsername(String activityObject) {
        if (!isUser(activityObject)) {
            throw new IllegalArgumentException(activityObject + " is not a user activity object");
        }
        return activityObject.replaceAll(USER_PREFIX, "");
    }

    public static List<String> getUsernames(List<String> activityObjects) {
        List<String> usernames = new ArrayList<String>();
        for (String activityObject : activityObjects) {
            usernames.add(getUsername(activityObject));
        }
        return usernames;
    }

    public static String getDocumentId(String activityObject) {
        if (isDocument(activityObject)) {
            String[] v = activityObject.split(":");
            return v[2];
        }
        return "";
    }

    public static String getRepositoryName(String activityObject) {
        if (isDocument(activityObject)) {
            String[] v = activityObject.split(":");
            return v[1];
        }
        return "";
    }

    public static String getActivityId(String activityObject) {
        if (isActivity(activityObject)) {
            String[] v = activityObject.split(":");
            return v[1];
        }
        return "";
    }

    public static String createDocumentActivityObject(DocumentModel doc) {
        return createDocumentActivityObject(doc.getRepositoryName(), doc.getId());
    }

    public static String createDocumentActivityObject(String repositoryName, String docId) {
        return DOC_PREFIX + repositoryName + SEPARATOR + docId;
    }

    public static String createUserActivityObject(NuxeoPrincipal principal) {
        return createUserActivityObject(principal.getName());
    }

    public static String createUserActivityObject(String username) {
        return USER_PREFIX + username;
    }

    public static String createActivityObject(Activity activity) {
        return createActivityObject(activity.getId());
    }

    public static String createActivityObject(Serializable activityId) {
        return ACTIVITY_PREFIX + activityId;
    }

    public static String generateDisplayName(NuxeoPrincipal principal) {
        if (!StringUtils.isBlank(principal.getFirstName()) || !StringUtils.isBlank(principal.getLastName())) {
            return principal.getFirstName() + " " + principal.getLastName();
        }
        return principal.getName();
    }

    public static String getDocumentTitle(DocumentModel doc) {
        try {
            return doc.getTitle();
        } catch (PropertyException e) {
            return doc.getId();
        }
    }

}
