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
 *      André Justo
 */

package org.nuxeo.ecm.media.publishing.wistia.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.MultiPart;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;

public class RestRequest {

    protected WebResource service;

    protected String path;

    protected RequestType requestType = RequestType.GET;

    protected String data;

    protected Map<String, Object> headers = new HashMap<String, Object>();

    protected MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

    protected String contentType = MediaType.APPLICATION_JSON;

    public RestRequest(WebResource service, String path) {
        this.service = service;
        this.path = path;
    }

    public RestRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public RestRequest headers(Map<String, Object> headers) {
        this.headers = headers;
        return this;
    }

    public RestRequest queryParam(String key, String value) {
        this.queryParams.add(key, value);
        return this;
    }

    public RestRequest queryParams(MultivaluedMap<String, String> queryParams) {
        if (queryParams != null) {
            this.queryParams = queryParams;
        }
        return this;
    }

    public RestRequest requestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public RestRequest contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public String getData() {
        return this.data;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public MultivaluedMap<String, String> getQueryParams() {
        return this.queryParams;
    }

    public String getContentType() {
        return this.contentType;
    }

    public RestResponse execute(MultiPart multiPart) {
        WebResource wr = service;
        wr = wr.path(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            wr = wr.queryParams(queryParams);
        }

        WebResource.Builder builder = wr.type(contentType);
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        ClientResponse response = null;
        switch (requestType) {
            case GET:
                response = builder.get(ClientResponse.class);
                break;
            case POST:
                if (multiPart != null) {
                    response = builder.post(ClientResponse.class, multiPart);
                }
                else {
                    response = builder.post(ClientResponse.class, data);
                }
                break;
            case PUT:
                response = builder.put(ClientResponse.class, data);
                break;
            case DELETE:
                response = builder.delete(ClientResponse.class, data);
                break;
        }
        return new RestResponse(response);
    }

    public RestResponse execute() {
        return execute(null);
    }
}
