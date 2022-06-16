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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import org.nuxeo.ecm.media.publishing.wistia.model.Media;
import org.nuxeo.ecm.media.publishing.wistia.model.Project;
import org.nuxeo.ecm.media.publishing.wistia.model.Account;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class WistiaResponseParser {

    static ObjectMapper mapper = new ObjectMapper();

    public static Project asProject(ClientResponse clientResponse) {
        Project project;
        try {
            project = mapper.readValue(clientResponse.getEntityInputStream(), Project.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    public static List<Project> asProjectList(ClientResponse clientResponse) {
        List<Project> projects;
        ObjectMapper mapper = new ObjectMapper();
        try {
            projects = mapper.readValue(clientResponse.getEntityInputStream(), mapper.getTypeFactory().constructCollectionType(List.class, Project.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

    public static Media asMedia(ClientResponse clientResponse) {
        Media media;
        if (clientResponse.getStatusInfo().getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        try {
            media = mapper.readValue(clientResponse.getEntityInputStream(), Media.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return media;
    }

    public static List<Media> asMediaList(ClientResponse clientResponse) {
        List<Media> medias;
        try {
            medias = mapper.readValue(clientResponse.getEntityInputStream(), mapper.getTypeFactory().constructCollectionType(List.class, Media.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return medias;
    }

    public static Account asAccount(ClientResponse clientResponse) {
        Account account;
        ObjectMapper mapper = new ObjectMapper();
        try {
            account = mapper.readValue(clientResponse.getEntityInputStream(), Account.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return account;
    }
}
