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

package org.nuxeo.ecm.media.publishing.upload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.event.DocumentEventCategories;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.ecm.media.publishing.MediaPublishingConstants;
import org.nuxeo.ecm.media.publishing.MediaPublishingProvider;
import org.nuxeo.ecm.media.publishing.adapter.PublishableMedia;
import org.nuxeo.runtime.api.Framework;

/**
 * Work for asynchronous media upload.
 *
 * @since 7.3
 */
public class MediaPublishingUploadWork extends AbstractWork {
    public static final String CATEGORY_VIDEO_UPLOAD = "mediaPublishingUpload";

    private final String serviceId;

    private final MediaPublishingProvider service;

    private CoreSession loginSession;

    private String account;

    private Map<String, String> options;

    public MediaPublishingUploadWork(String serviceId, MediaPublishingProvider service, String repositoryName,
            String docId, CoreSession loginSession, String account, Map<String, String> options) {
        super(getIdFor(repositoryName, docId, serviceId));
        this.serviceId = serviceId;
        this.service = service;
        this.loginSession = loginSession;
        this.account = account;
        this.options = options;
        setDocument(repositoryName, docId);
    }

    public static String getIdFor(String repositoryName, String docId, String provider) {
        return "media_" + provider + "_upload_" + repositoryName + "_" + docId;
    }

    @Override
    public String getCategory() {
        return CATEGORY_VIDEO_UPLOAD;
    }

    @Override
    public String getTitle() {
        return "Video Upload: " + docId;
    }

    @Override
    public void work() {
        final IdRef idRef = new IdRef(docId);
        new UnrestrictedSessionRunner(repositoryName) {
            @Override
            public void run() {
                final DocumentModel doc = session.getDocument(idRef);
                PublishableMedia media = doc.getAdapter(PublishableMedia.class);

                MediaPublishingProgressListener listener = new MediaPublishingProgressListener() {
                    @Override
                    public void onStart() {
                        setProgress(Progress.PROGRESS_0_PC);
                    }

                    @Override
                    public void onProgress(double progress) {
                        setProgress(new Progress(new Float(progress)));
                    }

                    @Override
                    public void onComplete() {
                        setProgress(Progress.PROGRESS_100_PC);
                    }

                    @Override
                    public void onError() {
                        setStatus("Error");
                    }
                };
                try {
                    String mediaId = service.upload(media, listener, account, options);
                    Map<String, Object> entry = new HashMap<>();
                    entry.put(MediaPublishingConstants.ID_PROPERTY_NAME, mediaId);
                    entry.put(MediaPublishingConstants.PROVIDER_PROPERTY_NAME, serviceId);
                    entry.put(MediaPublishingConstants.ACCOUNT_PROPERTY_NAME, account);
                    media.putProvider(entry);

                    // We don't want to erase the current version
                    doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.NONE);
                    doc.putContextData(VersioningService.DISABLE_AUTO_CHECKOUT, Boolean.TRUE);

                    // Track media publication in document history
                    DocumentEventContext ctx = new DocumentEventContext(loginSession, loginSession.getPrincipal(), doc);
                    ctx.setComment("Published to " + serviceId);
                    ctx.setCategory(DocumentEventCategories.EVENT_DOCUMENT_CATEGORY);

                    EventProducer evtProducer = Framework.getService(EventProducer.class);
                    Event event = ctx.newEvent(DocumentEventTypes.DOCUMENT_PUBLISHED);
                    evtProducer.fireEvent(event);
                    doc.getCoreSession().saveDocument(doc);
                    session.save();
                } catch (IOException e) {
                    throw new NuxeoException("Failed to upload media", e);
                }

            }
        }.runUnrestricted();

    }
}
