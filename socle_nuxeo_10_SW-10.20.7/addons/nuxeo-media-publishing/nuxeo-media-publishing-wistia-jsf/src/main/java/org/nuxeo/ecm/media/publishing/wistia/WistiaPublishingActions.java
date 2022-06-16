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
 *      Andre Justo
 */

package org.nuxeo.ecm.media.publishing.wistia;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.media.publishing.MediaPublishingProvider;
import org.nuxeo.ecm.media.publishing.MediaPublishingService;
import org.nuxeo.ecm.media.publishing.wistia.model.Project;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.List;

/**
 * @since 7.3
 */
@Name("wistiaPublishingActions")
@Scope(ScopeType.EVENT)
public class WistiaPublishingActions implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(WistiaPublishingActions.class);

    List<Project> projects;

    /**
     * Helper to retrieve a list of projects for a given Wistia account
     */
    public List<Project> getProjects(String account) {

        if (account == null || account.length() == 0) {
            return null;
        }

        if (projects == null) {
            MediaPublishingProvider service = getMediaPublishingService().getProvider("Wistia");
            projects = ((WistiaService) service).getProjects(account);
        }

        return projects;
    }

    private MediaPublishingService getMediaPublishingService() {
        return Framework.getService(MediaPublishingService.class);
    }
}
