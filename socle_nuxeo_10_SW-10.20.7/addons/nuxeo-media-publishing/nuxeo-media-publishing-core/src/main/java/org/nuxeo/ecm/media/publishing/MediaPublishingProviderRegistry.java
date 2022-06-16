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
 *      Nelson Silva
 */

package org.nuxeo.ecm.media.publishing;

import org.nuxeo.runtime.model.ContributionFragmentRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @since 7.3
 */
public class MediaPublishingProviderRegistry extends ContributionFragmentRegistry<MediaPublishingProviderDescriptor> {

    protected final Map<String, MediaPublishingProviderDescriptor> providers = new HashMap<>();

    @Override
    public MediaPublishingProviderDescriptor clone(MediaPublishingProviderDescriptor source) {
        MediaPublishingProviderDescriptor copy = new MediaPublishingProviderDescriptor();
        // TODO
        return copy;
    }

    @Override
    public void contributionRemoved(String name, MediaPublishingProviderDescriptor origContrib) {
        providers.remove(name);
    }

    @Override
    public void contributionUpdated(String name, MediaPublishingProviderDescriptor contrib,
        MediaPublishingProviderDescriptor newOrigContrib) {
        if (newOrigContrib.isEnabled()) {
            providers.put(name, newOrigContrib);
        } else {
            providers.remove(name);
        }
    }

    @Override
    public String getContributionId(MediaPublishingProviderDescriptor contrib) {
        return contrib.getId();
    }

    @Override
    public void merge(MediaPublishingProviderDescriptor src, MediaPublishingProviderDescriptor dst) {
        // TODO
    }

    public Set<String> getServices() {
        Set<String> services = new HashSet<>();
        for (String provider : providers.keySet()) {
            if (lookup(provider).isEnabled()) {
                services.add(provider);
            }
        }
        return services;
    }

    public MediaPublishingProviderDescriptor lookup(String provider) {
        return providers.get(provider);
    }
}

