/*
 * (C) Copyright 2006-2007 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.nuxeo.ecm.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.http.client.authentication.PortalSSOAuthenticationProvider;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

public class NuxeoServer {

    public static final int AUTH_TYPE_NONE = 0;

    public static final int AUTH_TYPE_BASIC = 1;

    public static final int AUTH_TYPE_SECRET = 2;

    protected String baseURL = "http://127.0.0.1:8080/nuxeo";

    protected String restPrefix = "restAPI";

    protected String davPrefix = "dav";

    protected List<Cookie> cookies;

    protected int authType = AUTH_TYPE_NONE;

    protected String userName;

    protected String password;

    protected String secretToken;

    protected Client restClient;

    public NuxeoServer(String baseURL) {
        this.baseURL = baseURL;
    }

    public NuxeoServer(String protocol, String serverIP, String serverPort) {
        this(protocol, serverIP, serverPort, "nuxeo");
    }

    public NuxeoServer(String protocol, String serverIP, String serverPort, String servletPath) {
        StringBuffer sb = new StringBuffer();
        sb.append(protocol);
        sb.append("://");
        sb.append(serverIP);
        if (serverPort != null && !serverIP.equals("80")) {
            sb.append(':');
            sb.append(serverPort);
        }
        sb.append(servletPath);
        sb.append('/');
        baseURL = sb.toString();
    }

    public void setBasicAuthentication(String userName, String password) {
        authType = AUTH_TYPE_BASIC;
        this.userName = userName;
        this.password = password;
    }

    public void setSharedSecretAuthentication(String userName, String secretToken) {
        authType = AUTH_TYPE_SECRET;
        this.userName = userName;
        this.secretToken = secretToken;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * @deprecated Use {@link NuxeoServer#doRestletPostCall(List, Map, InputStream, Long)} and provide a length.
     * @param pathParams
     * @param queryParams
     * @param istream
     * @return
     */
    public Representation doRestletPostCall(List<String> pathParams, Map<String, String> queryParams,
            InputStream istream) {
        return doRestletPostCall(pathParams, queryParams, istream, null);
    }

    public Representation doRestletPostCall(List<String> pathParams, Map<String, String> queryParams,
            InputStream istream, Long size) {
        String path = "";
        StringBuffer pathBuffer = new StringBuffer();

        if (pathParams != null) {
            for (String p : pathParams) {
                pathBuffer.append(p);
                pathBuffer.append('/');
            }
            path = pathBuffer.toString();
        }

        return doRestletPostCall(path, queryParams, istream, size);
    }

    public Representation doRestletPostCall(String subPath, Map<String, String> queryParams, InputStream istream,
            Long size) {
        StringBuffer urlBuffer = new StringBuffer();

        if (subPath.startsWith("/")) {
            subPath = subPath.substring(1);
        }
        if (subPath.endsWith("/")) {
            subPath = subPath.substring(0, subPath.length() - 1);
        }

        urlBuffer.append(baseURL);
        urlBuffer.append('/');
        urlBuffer.append(restPrefix);
        urlBuffer.append('/');
        urlBuffer.append(subPath);

        if (queryParams != null) {
            urlBuffer.append('?');
            for (String qpName : queryParams.keySet()) {
                urlBuffer.append(qpName);
                urlBuffer.append('=');
                urlBuffer.append(queryParams.get(qpName).replaceAll(" ", "%20"));
                urlBuffer.append('&');
            }
        }

        String completeURL = urlBuffer.toString();

        Request request = new Request(Method.POST, completeURL);

        setupAuth(request);
        setupCookies(request);
        final InputStream in = istream;
        OutputRepresentation representation = new OutputRepresentation(MediaType.MULTIPART_FORM_DATA) {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                byte[] buffer = new byte[1024 * 64];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

            }
        };
        request.setEntity(representation);
        if (size != null) {
            representation.setSize(size);
        }

        return getRestClient().handle(request).getEntity();
    }

    public Representation doRestletGetCall(List<String> pathParams, Map<String, String> queryParams) {
        String path = "";
        StringBuffer pathBuffer = new StringBuffer();

        if (pathParams != null) {
            for (String p : pathParams) {
                pathBuffer.append(p);
                pathBuffer.append('/');
            }
            path = pathBuffer.toString();
        }

        return doRestletGetCall(path, queryParams);
    }

    public Representation doRestletGetCall(String subPath, Map<String, String> queryParams) {
        StringBuffer urlBuffer = new StringBuffer();

        if (subPath.startsWith("/")) {
            subPath = subPath.substring(1);
        }
        if (subPath.endsWith("/")) {
            subPath = subPath.substring(0, subPath.length() - 1);
        }

        urlBuffer.append(baseURL);
        urlBuffer.append('/');
        urlBuffer.append(restPrefix);
        urlBuffer.append('/');
        urlBuffer.append(subPath);

        if (queryParams != null) {
            urlBuffer.append('?');
            for (String qpName : queryParams.keySet()) {
                urlBuffer.append(qpName);
                urlBuffer.append('=');
                urlBuffer.append(queryParams.get(qpName).replaceAll(" ", "%20"));
                urlBuffer.append('&');
            }
        }

        String completeURL = urlBuffer.toString();

        Request request = new Request(Method.GET, completeURL);
        setupAuth(request);
        setupCookies(request);

        Response response = getRestClient().handle(request);
        if (response.getLocationRef() != null) {
            request = new Request(Method.GET, response.getLocationRef());
            setupAuth(request);
            setupCookies(request);
            response = getRestClient().handle(request);
        }
        return response.getEntity();
    }

    protected void setupAuth(Request request) {

        if (authType == AUTH_TYPE_BASIC) {
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme, userName, password);
            request.setChallengeResponse(authentication);

        } else if (authType == AUTH_TYPE_SECRET) {
            Series<Parameter> additionnalHeaders = new Form();

            Map<String, String> securityHeaders = PortalSSOAuthenticationProvider.getHeaders(secretToken, userName);

            for (String hn : securityHeaders.keySet()) {
                additionnalHeaders.add(hn, securityHeaders.get(hn));
            }

            request.getAttributes().put("org.restlet.http.headers", additionnalHeaders);
        }
    }

    protected void setupCookies(Request request) {
        if (cookies != null) {
            request.getCookies().clear();
            for (Cookie cookie : cookies) {
                request.getCookies().add(cookie);
            }
        }

    }

    protected Client getRestClient() {
        if (restClient == null) {
            if (baseURL.startsWith("https")) {
                restClient = new Client(Protocol.HTTPS);
            } else {
                restClient = new Client(Protocol.HTTP);
            }
        }

        return restClient;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getDavPrefix() {
        return davPrefix;
    }

    public void setDavPrefix(String davPrefix) {
        this.davPrefix = davPrefix;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRestPrefix() {
        return restPrefix;
    }

    public void setRestPrefix(String restPrefix) {
        this.restPrefix = restPrefix;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
