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

import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;

/**
 * Operation to remove an activity reply.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
@Operation(id = RemoveActivityReply.ID, category = Constants.CAT_SERVICES, label = "Remove a reply to an existing activity", description = "Remove a reply to an existing activity.")
public class RemoveActivityReply {

    public static final String ID = "Services.RemoveActivityReply";

    @Context
    protected ActivityStreamService activityStreamService;

    @Param(name = "activityId", required = true)
    protected String activityId;

    @Param(name = "replyId", required = true)
    protected String replyId;

    @OperationMethod
    public void run() throws NumberFormatException {
        activityStreamService.removeActivityReply(Long.valueOf(activityId), replyId);
    }

}
