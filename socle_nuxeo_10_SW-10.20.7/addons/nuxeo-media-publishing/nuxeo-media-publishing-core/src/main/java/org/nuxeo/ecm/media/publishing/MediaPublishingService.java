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

import org.nuxeo.ecm.core.api.DocumentModel;
import java.util.Map;

/**
 * @since 7.3
 */
public interface MediaPublishingService {

    /**
     * Return a list of the available media publishing services for the given document
     */
    String[] getAvailableProviders(DocumentModel doc);

    /**
     * Schedules an upload
     * @param doc the Document to upload
     * @param provider the id of the media publishing provider
     * @return the id of the publishing work
     */
    String publish(DocumentModel doc, String provider, String account, Map<String, String> options);

    /**
     * Unpublish the media
     *
     * @since 7.4
     */
    void unpublish(DocumentModel doc, String provider);

    /**
     * Return the provider with the given name.
     */
    MediaPublishingProvider getProvider(String provider);
}
