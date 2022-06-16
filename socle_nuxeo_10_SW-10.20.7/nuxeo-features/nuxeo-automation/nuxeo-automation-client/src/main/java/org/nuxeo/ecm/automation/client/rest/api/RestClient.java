/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger
 */

package org.nuxeo.ecm.automation.client.rest.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.client.BasicCookieStore;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;

/**
 * REST client used to to requests on the Nuxeo REST API.
 *
 * @since 5.8
 */
public class RestClient {

    protected static final String SITE_AUTOMATION_PATH_PATTERN = "(\\/api\\/v1)?(\\/site)?\\/automation";

    private static final Pattern SITE_AUTOMATION_PATH_PATTERN_COMPILED = Pattern.compile(SITE_AUTOMATION_PATH_PATTERN,
            Pattern.CASE_INSENSITIVE);

    protected static final String API_PATH = "/api/v1";

    WebResource service;

    public RestClient(HttpAutomationClient httpAutomationClient) {
        ApacheHttpClient4Handler handler = new ApacheHttpClient4Handler(httpAutomationClient.http(),
                new BasicCookieStore(), false);
        ApacheHttpClient4 client = new ApacheHttpClient4(handler);

        if (httpAutomationClient.getRequestInterceptor() != null) {
            client.addFilter(httpAutomationClient.getRequestInterceptor());
        }

        String apiURL = httpAutomationClient.getBaseUrl();
        apiURL = replaceAutomationEndpoint(apiURL);
        service = client.resource(apiURL);
    }

    private String replaceAutomationEndpoint(String url) {
        Matcher matcher = SITE_AUTOMATION_PATH_PATTERN_COMPILED.matcher(url);
        return matcher.replaceAll(API_PATH);
    }

    public RestRequest newRequest(String path) {
        return new RestRequest(service, path);
    }

}
