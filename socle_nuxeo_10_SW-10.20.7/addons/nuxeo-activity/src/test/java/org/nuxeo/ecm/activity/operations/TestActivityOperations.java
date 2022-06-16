/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.ecm.activity.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.activity.Activity;
import org.nuxeo.ecm.activity.ActivityReply;
import org.nuxeo.ecm.activity.ActivityFeature;
import org.nuxeo.ecm.activity.ActivityImpl;
import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.activity.ActivityStreamServiceImpl;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
@RunWith(FeaturesRunner.class)
@Features(ActivityFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.automation.core")
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
@Deploy("org.nuxeo.ecm.user.center.profile")
public class TestActivityOperations {

    @Inject
    protected CoreSession session;

    @Inject
    protected ActivityStreamService activityStreamService;

    @Inject
    protected AutomationService automationService;

    protected int getOffset() {
        return activityStreamService.query(ActivityStreamService.ALL_ACTIVITIES, null).size();
    }

    @Test
    @Ignore
    public void shouldAddAnActivityReply() throws Exception {
        Activity activity = new ActivityImpl();
        activity.setActor("Administrator");
        activity.setVerb("test");
        activity.setObject("yo");
        activity.setPublishedDate(new Date());
        activity = activityStreamService.addActivity(activity);

        String replyMessage = "First reply";

        OperationContext ctx = new OperationContext(session);
        assertNotNull(ctx);

        OperationChain chain = new OperationChain("testActivityOperation");
        chain.add(AddActivityReply.ID).set("activityId", String.valueOf(activity.getId())).set("message", replyMessage);
        Blob result = (Blob) automationService.run(ctx, chain);
        assertNotNull(result);
        String json = result.getString();
        assertNotNull(json);

        List<Activity> activities = activityStreamService.query(ActivityStreamService.ALL_ACTIVITIES, null);
        assertNotNull(activities);
        assertEquals(1, activities.size());

        activity = activities.get(0);
        List<ActivityReply> replies = activity.getActivityReplies();
        assertFalse(replies.isEmpty());
        assertEquals(1, replies.size());
        ActivityReply reply = replies.get(0);
        assertEquals(activity.getId() + "-reply-1", reply.getId());
        assertEquals("user:Administrator", reply.getActor());
        assertEquals("Administrator", reply.getDisplayActor());
        assertNotNull(reply.getPublishedDate());
        assertEquals("First reply", reply.getMessage());
    }

    @Test
    public void shouldRemoveAnActivityReply() throws Exception {
        int offset = getOffset();

        Activity activity = new ActivityImpl();
        activity.setActor("Administrator");
        activity.setVerb("test");
        activity.setObject("yo");
        activity.setPublishedDate(new Date());
        activity = activityStreamService.addActivity(activity);

        long firstReplyPublishedDate = new Date().getTime();
        ActivityReply firstReply = new ActivityReply("bender", "Bender", "First reply", firstReplyPublishedDate);
        firstReply = activityStreamService.addActivityReply(activity.getId(), firstReply);
        long secondReplyPublishedDate = new Date().getTime();
        ActivityReply secondReply = new ActivityReply("bender", "Bender", "Second reply", secondReplyPublishedDate);
        secondReply = activityStreamService.addActivityReply(activity.getId(), secondReply);

        List<Activity> activities = activityStreamService.query(ActivityStreamService.ALL_ACTIVITIES, null, offset, 999);
        assertNotNull(activities);
        assertEquals(1, activities.size());
        activity = activities.get(0);
        List<ActivityReply> replies = activity.getActivityReplies();
        assertFalse(replies.isEmpty());
        assertEquals(2, replies.size());

        OperationContext ctx = new OperationContext(session);
        assertNotNull(ctx);

        OperationChain chain = new OperationChain("testActivityOperation");
        chain.add(RemoveActivityReply.ID).set("activityId", String.valueOf(activity.getId())).set("replyId",
                secondReply.getId());
        automationService.run(ctx, chain);

        activities = activityStreamService.query(ActivityStreamService.ALL_ACTIVITIES, null, offset, 999);
        assertNotNull(activities);
        assertEquals(1, activities.size());
        activity = activities.get(0);
        replies = activity.getActivityReplies();
        assertFalse(replies.isEmpty());
        assertEquals(1, replies.size());

        ActivityReply reply = replies.get(0);
        assertEquals(activity.getId() + "-reply-1", reply.getId());
        assertEquals("bender", reply.getActor());
        assertEquals("Bender", reply.getDisplayActor());
        assertNotNull(reply.getPublishedDate());
        assertEquals("First reply", reply.getMessage());
    }

}
