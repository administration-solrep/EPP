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
 *     Nuxeo
 */

package org.nuxeo.ecm.showcase.content.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.URLBlob;
import org.nuxeo.ecm.showcase.content.ShowcaseContentImporter;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.SimpleContributionRegistry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 8.4
 */
public class ShowcaseContentServiceImpl extends DefaultComponent implements ShowcaseContentService {

    private static final Log log = LogFactory.getLog(ShowcaseContentServiceImpl.class);

    public static final String EP_CONTENTS = "contents";

    private ContributionFragmentRegistry<ShowcaseContentDescriptor> registry = new ShowcaseContentDescriptorSimpleContributionRegistry();

    private static class ShowcaseContentDescriptorSimpleContributionRegistry
            extends SimpleContributionRegistry<ShowcaseContentDescriptor> {
        @Override
        public String getContributionId(ShowcaseContentDescriptor contrib) {
            return contrib.getName();
        }
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (EP_CONTENTS.equals(extensionPoint)) {
            ShowcaseContentDescriptor content = (ShowcaseContentDescriptor) contribution;
            content.computeBlobUrl(contributor);
            registry.addContribution(content);
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (EP_CONTENTS.equals(extensionPoint)) {
            registry.removeContribution((ShowcaseContentDescriptor) contribution);
        }
    }

    @Override
    public void triggerImporters(CoreSession session) {
        getContributions().forEach(c -> {
            // XXX Should use a dedicated Worker...
            URLBlob blob = new URLBlob(c.blobUrl);
            try {
                ShowcaseContentImporter.run(session, c.getName(), blob);
            } catch (IOException e) {
                log.warn(String.format("Unable to import %s: %s", c.getName(), e), e);
            }
        });
    }

    protected List<ShowcaseContentDescriptor> getContributions() {
        return Arrays.stream(registry.getFragments())
                     .map(f -> f.object)
                     .filter(c -> c.enabled)
                     .collect(Collectors.toList());
    }
}
