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

import com.google.api.client.auth.oauth2.Credential;
import org.nuxeo.ecm.media.publishing.adapter.PublishableMedia;
import org.nuxeo.ecm.platform.oauth2.providers.NuxeoOAuth2ServiceProvider;
import org.nuxeo.ecm.platform.oauth2.providers.OAuth2ServiceProvider;
import org.nuxeo.ecm.platform.oauth2.providers.OAuth2ServiceProviderRegistry;
import org.nuxeo.runtime.api.Framework;

/**
 * Abstract Media Publishing Provider using OAuth2
 *
 * @since 7.3
 */
public abstract class OAuth2MediaPublishingProvider implements MediaPublishingProvider {

    private final String providerName;

    public OAuth2MediaPublishingProvider(String providerName) {
        this.providerName = providerName;
    }

    protected OAuth2ServiceProvider getOAuth2ServiceProvider() {
        OAuth2ServiceProviderRegistry oAuth2ProviderRegistry = Framework.getService(OAuth2ServiceProviderRegistry.class);
        return oAuth2ProviderRegistry.getProvider(providerName);
    }

    protected Credential getCredential(String account) {
        return getOAuth2ServiceProvider() != null ? getOAuth2ServiceProvider().loadCredential(account) : null;
    }

    @Override
    public boolean isAvailable() {
        NuxeoOAuth2ServiceProvider serviceProvider = (NuxeoOAuth2ServiceProvider) getOAuth2ServiceProvider();
        return serviceProvider != null && serviceProvider.isEnabled() &&
            serviceProvider.getClientSecret() != null && serviceProvider.getClientId() != null;
    }

    @Override
    public boolean isMediaAvailable(PublishableMedia media) {
        NuxeoOAuth2ServiceProvider serviceProvider = (NuxeoOAuth2ServiceProvider) getOAuth2ServiceProvider();
        String account = media.getAccount(providerName);
        return isAvailable() && serviceProvider != null && getCredential(account) != null;
    }
}
