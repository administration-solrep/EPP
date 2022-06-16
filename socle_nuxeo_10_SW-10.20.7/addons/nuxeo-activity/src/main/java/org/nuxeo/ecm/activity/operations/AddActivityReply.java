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

import static org.nuxeo.ecm.activity.ActivityHelper.getUsername;
import static org.nuxeo.ecm.activity.ActivityMessageHelper.replaceURLsByLinks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nuxeo.ecm.activity.ActivityHelper;
import org.nuxeo.ecm.activity.ActivityLinkBuilder;
import org.nuxeo.ecm.activity.ActivityReply;
import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Operation to add an activity reply.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
@Operation(id = AddActivityReply.ID, category = Constants.CAT_SERVICES, label = "Add a reply to an existing activity", description = "Add a reply to an existing activity.")
public class AddActivityReply {

    public static final String ID = "Services.AddActivityReply";

    @Context
    protected CoreSession session;

    @Context
    protected ActivityStreamService activityStreamService;

    @Param(name = "language", required = false)
    protected String language;

    @Param(name = "activityId", required = true)
    protected String activityId;

    @Param(name = "message", required = true)
    protected String message;

    @Param(name = "activityLinkBuilderName", required = true)
    protected String activityLinkBuilderName;

    @OperationMethod
    public Blob run() throws IOException {
        String actor = ActivityHelper.createUserActivityObject(session.getPrincipal());
        String displayActor = ActivityHelper.generateDisplayName(session.getPrincipal());
        ActivityReply reply = new ActivityReply(actor, displayActor, message, new Date().getTime());
        reply = activityStreamService.addActivityReply(Long.valueOf(activityId), reply);

        Locale locale = language != null && !language.isEmpty() ? new Locale(language) : Locale.ENGLISH;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        ActivityLinkBuilder activityLinkBuilder = Framework.getService(ActivityStreamService.class).getActivityLinkBuilder(
                activityLinkBuilderName);

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", reply.getId());
        m.put("actor", reply.getActor());
        m.put("displayActor", reply.getDisplayActor());
        m.put("displayActorLink", getDisplayActorLink(reply.getActor(), reply.getDisplayActor(), activityLinkBuilder));
        m.put("actorAvatarURL", activityLinkBuilder.getUserAvatarURL(session, getUsername(reply.getActor())));
        m.put("message", replaceURLsByLinks(reply.getMessage()));
        m.put("publishedDate", dateFormat.format(new Date(reply.getPublishedDate())));
        String username = ActivityHelper.getUsername(reply.getActor());
        m.put("allowDeletion", session.getPrincipal().getName().equals(username));

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, m);

        return Blobs.createJSONBlob(writer.toString());
    }

    protected String getDisplayActorLink(String actor, String displayActor, ActivityLinkBuilder activityLinkBuilder) {
        return activityLinkBuilder.getUserProfileLink(actor, displayActor);
    }

}
