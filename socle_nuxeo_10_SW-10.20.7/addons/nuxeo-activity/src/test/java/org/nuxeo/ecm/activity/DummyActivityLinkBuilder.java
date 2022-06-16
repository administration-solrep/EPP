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

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.6
 */
public class DummyActivityLinkBuilder implements ActivityLinkBuilder {

    @Override
    public String getDocumentLink(String documentActivityObject, String displayValue) {
        return "documentLink";
    }

    @Override
    public String getUserProfileLink(String userActivityObject, String displayValue) {
        return "userProfileLink";
    }

    @Override
    public String getUserAvatarURL(CoreSession session, String username) {
        return "userAvatarURL";
    }

}
