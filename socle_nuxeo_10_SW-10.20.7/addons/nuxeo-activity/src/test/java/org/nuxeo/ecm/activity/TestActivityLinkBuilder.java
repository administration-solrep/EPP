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

package org.nuxeo.ecm.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
@RunWith(FeaturesRunner.class)
@Features(ActivityFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
@Deploy("org.nuxeo.ecm.user.center.profile")
public class TestActivityLinkBuilder {

    @Inject
    protected ActivityStreamService activityStreamService;

    @Test
    public void shouldHaveADefaultActivityLinkBuilder() {
        ActivityLinkBuilder activityLinkBuilder = activityStreamService.getActivityLinkBuilder(null);
        assertNotNull(activityLinkBuilder);
    }

    @Test
    public void shouldRewriteLinkInActivityMessage() {
        Activity activity = new ActivityImpl();
        activity.setActor(ActivityHelper.createUserActivityObject("bender"));
        activity.setVerb("test");
        activity.setObject(ActivityHelper.createDocumentActivityObject("server", "docId"));
        activity.setPublishedDate(new Date());

        ActivityMessage activityMessage = activityStreamService.toActivityMessage(activity, Locale.ENGLISH, "dummy");
        assertEquals("userProfileLink", activityMessage.getDisplayActorLink());

        ActivityLinkBuilder dummyActivityLinkBuilder = activityStreamService.getActivityLinkBuilder("dummy");
        assertEquals("documentLink", dummyActivityLinkBuilder.getDocumentLink("server", "docId"));
        assertEquals("userAvatarURL", dummyActivityLinkBuilder.getUserAvatarURL(null, "bender"));
    }

}
