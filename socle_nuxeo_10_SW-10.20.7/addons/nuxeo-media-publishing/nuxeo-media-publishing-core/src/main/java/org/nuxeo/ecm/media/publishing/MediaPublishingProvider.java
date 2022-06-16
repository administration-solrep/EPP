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
 *      Nelson Silva
 */

package org.nuxeo.ecm.media.publishing;

import org.nuxeo.ecm.media.publishing.adapter.PublishableMedia;
import org.nuxeo.ecm.media.publishing.upload.MediaPublishingProgressListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface to be implemented by Media Publishing Providers.
 *
 * @since 7.3
 */
public interface MediaPublishingProvider {

    /**
     * Upload the media
     */
    String upload(PublishableMedia media, MediaPublishingProgressListener progressListener, String account, Map<String, String> options) throws IOException;

    /**
     * Unpublish the media
     *
     * @since 7.4
     */
    boolean unpublish(PublishableMedia media) throws IOException;

    /**
     * Retrieve the URL for the published media
     */
    String getPublishedUrl(String mediaId, String account);

    /**
     * Retrieve the HTML code for embedding the media
     */
    String getEmbedCode(String mediaId, String account);

    /**
     * Retrieve a map of statistics (depends on the provider)
     */
    Map<String, String> getStats(String mediaId, String account);

    /**
     * Checks if the provider is available
     */
    boolean isAvailable();

    /**
     * Checks it the given {@link PublishableMedia} is acessible
     */
    boolean isMediaAvailable(PublishableMedia media);

    /**
     * Checks if a media is published by the provider
     */
    boolean isMediaPublished(String mediaId, String account);
}
