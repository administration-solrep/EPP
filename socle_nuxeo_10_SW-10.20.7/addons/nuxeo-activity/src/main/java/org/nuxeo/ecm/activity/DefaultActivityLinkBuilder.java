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

import static org.nuxeo.ecm.user.center.profile.UserProfileConstants.USER_PROFILE_AVATAR_FIELD;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentLocationImpl;
import org.nuxeo.ecm.core.io.download.DownloadService;
import org.nuxeo.ecm.platform.ui.web.rest.api.URLPolicyService;
import org.nuxeo.ecm.platform.url.DocumentViewImpl;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.runtime.api.Framework;

/**
 * Default {@link ActivityLinkBuilder} computing URLs with the default id codec for documents and user codec for users.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
public class DefaultActivityLinkBuilder implements ActivityLinkBuilder {

    @Override
    public String getDocumentLink(String documentActivityObject, String displayValue) {
        documentActivityObject = StringEscapeUtils.escapeHtml4(documentActivityObject);
        displayValue = StringEscapeUtils.escapeHtml4(displayValue);
        String link = "<a href=\"%s\" target=\"_top\">%s</a>";
        return String.format(link, getDocumentURL(ActivityHelper.getRepositoryName(documentActivityObject),
                ActivityHelper.getDocumentId(documentActivityObject)), displayValue);
    }

    protected String getDocumentURL(String repositoryName, String documentId) {
        DocumentLocation docLoc = new DocumentLocationImpl(repositoryName, new IdRef(documentId));
        DocumentView docView = new DocumentViewImpl(docLoc, "view_documents");
        URLPolicyService urlPolicyService = Framework.getService(URLPolicyService.class);
        return urlPolicyService.getUrlFromDocumentView("id", docView, VirtualHostHelper.getContextPathProperty());
    }

    @Override
    public String getUserProfileLink(String userActivityObject, String displayValue) {
        userActivityObject = StringEscapeUtils.escapeHtml4(userActivityObject);
        displayValue = StringEscapeUtils.escapeHtml4(displayValue);
        String link = "<span class=\"username\"><a href=\"%s\" target=\"_top\" title=\"%s\">%s</a></span>";
        String username = ActivityHelper.getUsername(userActivityObject);
        return String.format(link, getUserProfileURL(username), username, displayValue);
    }

    protected String getUserProfileURL(String username) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        DocumentView docView = new DocumentViewImpl(null, null, params);
        URLPolicyService urlPolicyService = Framework.getService(URLPolicyService.class);
        if (urlPolicyService == null) {
            return "";
        }
        return urlPolicyService.getUrlFromDocumentView("user", docView, VirtualHostHelper.getContextPathProperty());
    }

    @Override
    public String getUserAvatarURL(CoreSession session, String username) {
        UserProfileService userProfileService = Framework.getService(UserProfileService.class);
        DocumentModel profile = userProfileService.getUserProfileDocument(username, session);
        Blob avatar = (Blob) profile.getPropertyValue(USER_PROFILE_AVATAR_FIELD);
        if (avatar != null) {
            DownloadService downloadService = Framework.getService(DownloadService.class);
            String filename = username + "." + FilenameUtils.getExtension(avatar.getFilename());
            return VirtualHostHelper.getContextPathProperty() + "/"
                    + downloadService.getDownloadUrl(profile, USER_PROFILE_AVATAR_FIELD, filename);
        } else {
            return VirtualHostHelper.getContextPathProperty() + "/icons/missing_avatar.png";
        }
    }
}
