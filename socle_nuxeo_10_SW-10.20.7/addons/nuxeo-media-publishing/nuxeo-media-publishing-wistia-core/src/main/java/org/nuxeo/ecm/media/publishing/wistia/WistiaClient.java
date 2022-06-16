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

package org.nuxeo.ecm.media.publishing.wistia;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;
import org.nuxeo.ecm.media.publishing.wistia.model.Account;
import org.nuxeo.ecm.media.publishing.wistia.model.Media;
import org.nuxeo.ecm.media.publishing.wistia.model.Project;
import org.nuxeo.ecm.media.publishing.wistia.model.Stats;
import org.nuxeo.ecm.media.publishing.wistia.rest.RestRequest;
import org.nuxeo.ecm.media.publishing.wistia.rest.RestResponse;
import org.nuxeo.ecm.media.publishing.wistia.rest.WistiaResponseParser;
import org.nuxeo.ecm.media.publishing.wistia.rest.RequestType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class WistiaClient {

    private static String BASE_URL = "https://api.wistia.com/v1";

    private static String BASE_UPLOAD_URL = "https://upload.wistia.com";

    private static String BASE_EMBED_URL = "http://fast.wistia.net";

    protected String apiPassword;

    protected WebResource dataService;

    protected WebResource uploadService;

    protected WebResource embedService;

    public WistiaClient(String apiPassword) {
        this.apiPassword = apiPassword;
        dataService = new Client().resource(BASE_URL).queryParam("api_password", apiPassword);
        uploadService = new Client().resource(BASE_UPLOAD_URL).queryParam("api_password", apiPassword);
        embedService = new Client().resource(BASE_EMBED_URL);
    }

    /**
     * Obtains a list of all the media in an account.
     * @return
     */
    public List<Media> getMedias() {
        RestResponse response = new RestRequest(dataService, "medias.json")
                .execute();

        return WistiaResponseParser.asMediaList(response.getClientResponse());
    }

    /**
     * Gets information about a specific piece of media uploaded to an account.
     * @param hashedId
     * @return
     */
    public Media getMedia(String hashedId) {
        RestResponse response = new RestRequest(dataService, "medias/" + hashedId + ".json")
                .execute();

        return WistiaResponseParser.asMedia(response.getClientResponse());
    }

    /**
     * Updates attributes on a piece of media.
     * @param hashedId
     * @param queryParams
     * @return
     */
    public Media updateMedia(String hashedId, MultivaluedMap<String,String> queryParams) {
        RestResponse response = new RestRequest(dataService, "medias/" + hashedId + ".json")
                .requestType(RequestType.PUT)
                .queryParams(queryParams)
                .execute();

        return WistiaResponseParser.asMedia(response.getClientResponse());
    }

    /**
     * Deletes a media from an account.
     * @param hashedId
     * @return
     */
    public Media deleteMedia(String hashedId) {
        RestResponse response = new RestRequest(dataService, "medias/" + hashedId + ".json")
                .requestType(RequestType.DELETE)
                .execute();

        return WistiaResponseParser.asMedia(response.getClientResponse());
    }

    /**
     * Aggregates tracking statistics for a video that has been embedded in a website.
     * @param hashedId
     * @return
     */
    public Stats getMediaStats(String hashedId) {
        RestResponse response = new RestRequest(dataService, "medias/" + hashedId + "/stats.json")
                .requestType(RequestType.GET)
                .execute();

        return WistiaResponseParser.asMedia(response.getClientResponse()).getStats();
    }

    /**
     * Gets information about an account.
     * @return
     */
    public Account getAccount() {
        RestResponse response = new RestRequest(dataService, "account.json")
                .execute();

        return WistiaResponseParser.asAccount(response.getClientResponse());
    }

    /**
     * Obtains a list of all the projects in an account.
     * @return
     */
    public List<Project> getProjects() {
        RestResponse response = new RestRequest(dataService, "projects.json")
                .execute();

        return WistiaResponseParser.asProjectList(response.getClientResponse());
    }

    /**
     * Gets information about a specific project.
     * @param hashedId
     * @return
     */
    public Project getProject(String hashedId) {
        RestResponse response = new RestRequest(dataService, "projects/" + hashedId + ".json")
                .execute();

        return WistiaResponseParser.asProject(response.getClientResponse());
    }

    /**
     * Creates a new project.
     * @param name
     * @param queryParams
     * @return
     */
    public Project createProject(String name, MultivaluedMap<String,String> queryParams) {
        RestResponse response = new RestRequest(dataService, "projects.json")
                .requestType(RequestType.POST)
                .queryParams(queryParams)
                .queryParam("name", name)
                .execute();

        return WistiaResponseParser.asProject(response.getClientResponse());
    }

    /**
     * Updates attributes on a project.
     * @param hashedId
     * @param queryParams
     * @return
     */
    public Project updateProject(String hashedId, MultivaluedMap<String,String> queryParams) {
        RestResponse response = new RestRequest(dataService, "projects/" + hashedId + ".json")
                .requestType(RequestType.PUT)
                .queryParams(queryParams)
                .execute();

        return WistiaResponseParser.asProject(response.getClientResponse());
    }

    /**
     * Deletes a project from an account.
     * @param hashedId
     * @return
     */
    public Project deleteProject(String hashedId) {
        RestResponse response = new RestRequest(dataService, "projects/" + hashedId + ".json")
                .requestType(RequestType.DELETE)
                .execute();

        return WistiaResponseParser.asProject(response.getClientResponse());
    }

    /**
     * Uploads a file from URL.
     * @param url
     * @param queryParams
     * @return
     */
    public Media upload(String url, MultivaluedMap<String, String> queryParams) {
        RestResponse response = new RestRequest(uploadService, "")
                .requestType(RequestType.POST)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .queryParams(queryParams)
                .queryParam("url", url)
                .execute();

        return WistiaResponseParser.asMedia(response.getClientResponse());
    }

    /**
     * Uploads a file.
     * @param file
     * @param queryParams
     * @return
     */
    public Media upload(File file, MultivaluedMap<String, String> queryParams) {
        FileDataBodyPart bodyPart = new FileDataBodyPart(file.getName(),
                file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return upload(bodyPart, queryParams);
    }

    /**
     * Uploads a file.
     * @param stream
     * @param queryParams
     * @return
     */
    public Media upload(String filename, InputStream stream, MultivaluedMap<String, String> queryParams) {
        StreamDataBodyPart bodyPart = new StreamDataBodyPart(filename,
            stream, filename, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return upload(bodyPart, queryParams);
    }

    private Media upload(BodyPart bodyPart, MultivaluedMap<String, String> queryParams) {
        MultiPart multiPart = new MultiPart();
        multiPart.bodyPart(bodyPart);

        RestResponse response = new RestRequest(uploadService, "")
            .requestType(RequestType.POST)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .queryParams(queryParams)
            .execute(multiPart);

        return WistiaResponseParser.asMedia(response.getClientResponse());
    }

    public String getEmbedCode(String mediaUrl) {
        RestResponse response = new RestRequest(embedService, "oembed")
                .queryParam("url", mediaUrl)
                .execute();

        if (response.getStatus() == 200) {
            return response.asJson().get("html").textValue();
        }

        return null;
    }
}
