/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger
 */

package org.nuxeo.ecm.platform.picture.listener;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.UPDATE_PICTURE_VIEW_EVENT;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitFilteringEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.picture.PictureViewsGenerationWork;
import org.nuxeo.runtime.api.Framework;

/**
 * Listener generating picture views.
 *
 * @since 5.7.2
 * @deprecated since 7.2
 */
@Deprecated
public class PictureViewListener implements PostCommitFilteringEventListener {
    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            if (UPDATE_PICTURE_VIEW_EVENT.equals(event.getName())) {
                handleEvent(event);
            }
        }
    }

    private void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();

        // launch work doing the actual views generation
        PictureViewsGenerationWork work = new PictureViewsGenerationWork(doc.getRepositoryName(),
                doc.getRef().toString(), "file:content");
        WorkManager workManager = Framework.getService(WorkManager.class);
        workManager.schedule(work, WorkManager.Scheduling.IF_NOT_SCHEDULED, true);
    }

    @Override
    public boolean acceptEvent(Event event) {
        return UPDATE_PICTURE_VIEW_EVENT.equals(event.getName());
    }
}
