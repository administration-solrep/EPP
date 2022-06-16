/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *      André Justo
 */

package org.nuxeo.ecm.media.publishing.wistia;

import com.google.api.client.auth.oauth2.Credential;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.media.publishing.wistia.model.Media;
import org.nuxeo.ecm.media.publishing.OAuth2MediaPublishingProvider;
import org.nuxeo.ecm.media.publishing.adapter.PublishableMedia;
import org.nuxeo.ecm.media.publishing.upload.MediaPublishingProgressListener;
import org.nuxeo.ecm.media.publishing.wistia.model.Project;
import org.nuxeo.ecm.media.publishing.wistia.model.Stats;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Wistia Media Publishing Provider Service
 *
 * @since 7.3
 */
public class WistiaService extends OAuth2MediaPublishingProvider {

    private static final Log log = LogFactory.getLog(WistiaService.class);

    public static final String PROVIDER = "Wistia";

    public WistiaService() {
        super(PROVIDER);
    }

    public WistiaClient getWistiaClient(String account) {
        Credential credential = getCredential(account);
        if (credential == null) {
            return null;
        }
        try {
            // Refresh access token if needed (based on com.google.api.client.auth.oauth.Credential.intercept())
            // TODO: rely on Google Oauth aware client instead
            Long expiresIn = credential.getExpiresInSeconds();
            // check if token will expire in a minute
            if (credential.getAccessToken() == null || expiresIn != null && expiresIn <= 60) {
                credential.refreshToken();
                if (credential.getAccessToken() == null) {
                    // nothing we can do without an access token
                    throw new NuxeoException("Failed to refresh access token");
                }
            }
        } catch (IOException e) {
            throw new NuxeoException(e.getMessage(), e);
        }

        return new WistiaClient(credential.getAccessToken());
    }

    @Override
    public String upload(PublishableMedia media, MediaPublishingProgressListener progressListener, String account, Map<String, String> options) throws IOException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        params.putSingle("name", media.getTitle());
        params.putSingle("description", media.getDescription());

        for (Entry<String, String> entry : options.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                params.putSingle(entry.getKey(), entry.getValue());
            }
        }

        // upload original video
        Blob blob = media.getBlob();

        Media video = getWistiaClient(account).upload(blob.getFilename(), blob.getStream(), params);

        return video.getHashedId();
    }

    @Override
    public boolean unpublish(PublishableMedia media) {
        String account = media.getAccount(PROVIDER);
        String mediaId = media.getId(PROVIDER);
        return getWistiaClient(account).deleteMedia(mediaId) != null;
    }

    @Override
    public String getPublishedUrl(String mediaId, String account) {
        WistiaClient client = getWistiaClient(account);
        return client == null ? null : client.getAccount().getUrl() + "/medias/" + mediaId;
    }

    @Override
    public String getEmbedCode(String mediaId, String account) {
        WistiaClient client = getWistiaClient(account);
        return client == null ? null : client.getEmbedCode(getPublishedUrl(mediaId, account));
    }

    @Override
    public Map<String, String> getStats(String mediaId, String account) {
        WistiaClient client = getWistiaClient(account);
        if (client == null) {
            return null;
        }

        Stats stats = client.getMediaStats(mediaId);
        if (stats == null) {
            return null;
        }

        Map<String, String> map = new HashMap<>();
        map.put("label.mediaPublishing.stats.visitors", Integer.toString(stats.getVisitors()));
        map.put("label.mediaPublishing.stats.plays", Integer.toString(stats.getPlays()));
        map.put("label.mediaPublishing.stats.averagePercentWatched", Integer.toString(stats.getAveragePercentWatched()));
        map.put("label.mediaPublishing.stats.pageLoads", Integer.toString(stats.getPageLoads()));
        map.put("label.mediaPublishing.stats.percentOfVisitorsClickingPlay", Integer.toString(stats.getPercentOfVisitorsClickingPlay()));
        return map;
    }

    @Override
    public boolean isMediaPublished(String mediaId, String account) {
        WistiaClient client = getWistiaClient(account);
        if (client == null) {
            return false;
        }

        Media media = client.getMedia(mediaId);
        return media != null;
    }

    public List<Project> getProjects(String account) {
        WistiaClient client = getWistiaClient(account);
        return client == null ? Collections.emptyList() : client.getProjects();
    }
}
