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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Helper class to compute links for {@link ActivityMessage} content from an activity attributes.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
public class ActivityMessageHelper {

    private ActivityMessageHelper() {
        // helper class
    }

    public static String getDocumentLink(String documentActivityObject, String displayValue) {
        documentActivityObject = StringEscapeUtils.escapeHtml4(documentActivityObject);
        displayValue = StringEscapeUtils.escapeHtml4(displayValue);
        String link = "<a href=\"%s\" target=\"_top\">%s</a>";
        return String.format(link, getDocumentURL(ActivityHelper.getRepositoryName(documentActivityObject),
                ActivityHelper.getDocumentId(documentActivityObject)), displayValue);
    }

    public static String getDocumentURL(String repositoryName, String documentId) {
        DocumentLocation docLoc = new DocumentLocationImpl(repositoryName, new IdRef(documentId));
        DocumentView docView = new DocumentViewImpl(docLoc, "view_documents");
        URLPolicyService urlPolicyService = Framework.getService(URLPolicyService.class);
        return urlPolicyService.getUrlFromDocumentView("id", docView, VirtualHostHelper.getContextPathProperty());
    }

    public static String getUserProfileLink(String userActivityObject, String displayValue) {
        userActivityObject = StringEscapeUtils.escapeHtml4(userActivityObject);
        displayValue = StringEscapeUtils.escapeHtml4(displayValue);
        String link = "<span class=\"username\"><a href=\"%s\" target=\"_top\" title=\"%s\">%s</a></span>";
        String username = ActivityHelper.getUsername(userActivityObject);
        return String.format(link, getUserProfileURL(username), username, displayValue);
    }

    public static String getUserProfileURL(String username) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        DocumentView docView = new DocumentViewImpl(null, null, params);
        URLPolicyService urlPolicyService = Framework.getService(URLPolicyService.class);
        return VirtualHostHelper.getContextPathProperty() + "/"
                + urlPolicyService.getUrlFromDocumentView("user", docView, null);
    }

    public static String getUserAvatarURL(CoreSession session, String username) {
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

    public static final Pattern HTTP_URL_PATTERN = Pattern.compile(
            "\\b(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");

    public static String replaceURLsByLinks(String message) {
        String escapedMessage = StringEscapeUtils.escapeHtml4(message);
        Matcher m = HTTP_URL_PATTERN.matcher(escapedMessage);
        StringBuffer sb = new StringBuffer(escapedMessage.length());
        while (m.find()) {
            String url = m.group(1);
            m.appendReplacement(sb, computeLinkFor(url));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String computeLinkFor(String url) {
        return "<a href=\"" + url + "\" target=\"_top\">" + url + "</a>";
    }

}
