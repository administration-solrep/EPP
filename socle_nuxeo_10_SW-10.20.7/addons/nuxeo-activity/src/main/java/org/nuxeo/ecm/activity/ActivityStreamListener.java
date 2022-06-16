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

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_CREATED;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_REMOVED;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_UPDATED;
import static org.nuxeo.ecm.core.schema.FacetNames.HIDDEN_IN_NAVIGATION;
import static org.nuxeo.ecm.core.schema.FacetNames.SUPER_SPACE;
import static org.nuxeo.ecm.core.schema.FacetNames.SYSTEM_DOCUMENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.SystemPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.ShallowDocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Listener called asynchronously to save events as activities through the {@link ActivityStreamService}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class ActivityStreamListener implements PostCommitEventListener {

    @Override
    public void handleEvent(EventBundle events) {
        if (isEventBundleHandled(events)) {
            List<Event> filteredEvents = filterDuplicateEvents(events);
            for (Event event : filteredEvents) {
                handleEvent(event);
            }
        }
    }

    protected List<Event> filterDuplicateEvents(EventBundle events) {
        List<Event> filteredEvents = new ArrayList<Event>();

        for (Event event : events) {
            filteredEvents = removeEventIfExist(filteredEvents, event);
            filteredEvents.add(event);
        }

        return filteredEvents;
    }

    protected List<Event> removeEventIfExist(List<Event> events, Event event) {
        EventContext eventContext = event.getContext();
        if (eventContext instanceof DocumentEventContext) {
            DocumentModel doc = ((DocumentEventContext) eventContext).getSourceDocument();
            for (Iterator<Event> it = events.iterator(); it.hasNext();) {
                Event filteredEvent = it.next();
                EventContext filteredEventContext = filteredEvent.getContext();
                if (filteredEventContext instanceof DocumentEventContext) {
                    DocumentModel filteredEventDoc = ((DocumentEventContext) filteredEventContext).getSourceDocument();
                    if (event.getName().equals(filteredEvent.getName())
                            && doc.getRef().equals(filteredEventDoc.getRef())) {
                        it.remove();
                        break;
                    }
                }
            }
        }
        return events;
    }

    protected void handleEvent(Event event) {
        EventContext eventContext = event.getContext();
        if (eventContext instanceof DocumentEventContext) {
            if (isEventHandled(event)) {
                DocumentEventContext docEventContext = (DocumentEventContext) eventContext;
                DocumentModel doc = docEventContext.getSourceDocument();
                if (isSkippedDocument(doc)) {
                    return;
                }

                if (docEventContext.getPrincipal() instanceof SystemPrincipal) {
                    // do not log activity for system principal
                    return;
                }

                // add activity without context
                ActivityStreamService activityStreamService = Framework.getService(ActivityStreamService.class);
                Activity activity = toActivity(docEventContext, event);
                activityStreamService.addActivity(activity);

                CoreSession session = docEventContext.getCoreSession();
                for (DocumentRef ref : getParentSuperSpaceRefs(session, doc)) {
                    String context = ActivityHelper.createDocumentActivityObject(session.getRepositoryName(),
                            ref.toString());
                    activity = toActivity(docEventContext, event, context);
                    activityStreamService.addActivity(activity);
                }
            }
        }
    }

    protected boolean isSkippedDocument(DocumentModel doc) {
        // Not really interested in non live document or if document
        // cannot be reconnected
        // or if not visible
        return doc instanceof ShallowDocumentModel || doc.hasFacet(HIDDEN_IN_NAVIGATION) //
                || doc.hasFacet(SYSTEM_DOCUMENT) //
                || doc.isProxy() //
                || doc.isVersion();
    }

    protected boolean isEventHandled(Event event) {
        for (String eventName : getHandledEventsName()) {
            if (eventName.equals(event.getName())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isEventBundleHandled(EventBundle events) {
        for (String eventName : getHandledEventsName()) {
            if (events.containsEventName(eventName)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> getHandledEventsName() {
        return Arrays.asList(DOCUMENT_CREATED, DOCUMENT_UPDATED, DOCUMENT_REMOVED);
    }

    protected Activity toActivity(DocumentEventContext docEventContext, Event event) {
        return toActivity(docEventContext, event, null);
    }

    protected Activity toActivity(DocumentEventContext docEventContext, Event event, String context) {
        NuxeoPrincipal principal = docEventContext.getPrincipal();
        DocumentModel doc = docEventContext.getSourceDocument();
        return new ActivityBuilder().actor(ActivityHelper.createUserActivityObject(principal)).displayActor(
                ActivityHelper.generateDisplayName(principal)).verb(event.getName()).object(
                ActivityHelper.createDocumentActivityObject(doc)).displayObject(ActivityHelper.getDocumentTitle(doc)).target(
                ActivityHelper.createDocumentActivityObject(doc.getRepositoryName(), doc.getParentRef().toString())).displayTarget(
                getDocumentTitle(docEventContext.getCoreSession(), doc.getParentRef())).context(context).build();
    }

    protected String getDocumentTitle(CoreSession session, DocumentRef docRef) {
        try {
            DocumentModel doc = session.getDocument(docRef);
            return ActivityHelper.getDocumentTitle(doc);
        } catch (DocumentNotFoundException e) {
            return docRef.toString();
        }
    }

    protected List<DocumentRef> getParentSuperSpaceRefs(CoreSession session, final DocumentModel doc)
            {
        final List<DocumentRef> parents = new ArrayList<DocumentRef>();
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() {
                List<DocumentModel> parentDocuments = session.getParentDocuments(doc.getRef());
                for (DocumentModel parent : parentDocuments) {
                    if (parent.hasFacet(SUPER_SPACE)) {
                        parents.add(parent.getRef());
                    }
                }
            }
        }.runUnrestricted();
        return parents;
    }

}
