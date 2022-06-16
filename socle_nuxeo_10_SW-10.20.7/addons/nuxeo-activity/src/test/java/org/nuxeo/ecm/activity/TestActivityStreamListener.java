/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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

import java.util.List;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_CREATED;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
@RunWith(FeaturesRunner.class)
@Features(ActivityFeature.class)
@Ignore
public class TestActivityStreamListener {

    @Inject
    protected CoreSession session;

    @Inject
    protected EventService eventService;

    @Inject
    protected ActivityStreamService activityStreamService;

    @Test
    public void shouldAddNewActivitiesThroughListener() {
        DocumentModel doc1 = session.createDocumentModel("/", "firstDocument", "File");
        doc1 = session.createDocument(doc1);
        commitAndWaitForAsyncCompletion();

        DocumentModel doc2 = session.createDocumentModel("/", "secondDocument", "File");
        doc2 = session.createDocument(doc2);
        commitAndWaitForAsyncCompletion();

        doc1.setPropertyValue("dc:title", "A new Title");
        session.saveDocument(doc1);
        commitAndWaitForAsyncCompletion();

        List<Activity> activities = activityStreamService.query(ActivityStreamService.ALL_ACTIVITIES, null);
        assertNotNull(activities);
        assertEquals(3, activities.size());

        String currentUser = ActivityHelper.createUserActivityObject(session.getPrincipal());
        Activity storedActivity = activities.get(0);
        assertEquals(1L, storedActivity.getId());
        assertEquals(currentUser, storedActivity.getActor());
        assertEquals(DOCUMENT_CREATED, storedActivity.getVerb());
        assertEquals(ActivityHelper.createDocumentActivityObject(doc1), storedActivity.getObject());
        assertEquals("firstDocument", storedActivity.getDisplayObject());
        assertEquals(ActivityHelper.createDocumentActivityObject(session.getRootDocument()), storedActivity.getTarget());

        storedActivity = activities.get(1);
        assertEquals(2L, storedActivity.getId());
        assertEquals(currentUser, storedActivity.getActor());
        assertEquals(DOCUMENT_CREATED, storedActivity.getVerb());
        assertEquals(ActivityHelper.createDocumentActivityObject(doc2), storedActivity.getObject());
        assertEquals("secondDocument", storedActivity.getDisplayObject());
        assertEquals(ActivityHelper.createDocumentActivityObject(session.getRootDocument()), storedActivity.getTarget());

        storedActivity = activities.get(2);
        assertEquals(3L, storedActivity.getId());
        assertEquals(currentUser, storedActivity.getActor());
        assertEquals(DocumentEventTypes.DOCUMENT_UPDATED, storedActivity.getVerb());
        assertEquals(ActivityHelper.createDocumentActivityObject(doc1), storedActivity.getObject());
        assertEquals("A new Title", storedActivity.getDisplayObject());
        assertEquals(ActivityHelper.createDocumentActivityObject(session.getRootDocument()), storedActivity.getTarget());
    }

    /**
     * Cannot play with, tx should be rollabacked
     */
    private void commitAndWaitForAsyncCompletion() {
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
    }

}
