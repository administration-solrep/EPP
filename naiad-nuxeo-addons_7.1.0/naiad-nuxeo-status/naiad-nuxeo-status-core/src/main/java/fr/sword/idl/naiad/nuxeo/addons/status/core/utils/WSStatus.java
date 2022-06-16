package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.nuxeo.runtime.api.Framework;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;

public class WSStatus extends AbstractParamStatusInfo {

    private static final String ENDPOINT_PREFIX = "endpoint=";
    private static final String PASSWORD_PREFIX = "password=";
    private static final String USER_PREFIX = "user=";

    @Override
    public Object getStatusInfo() {
        final String[] sParams = params.split(";");
        String user = null;
        String password = null;
        String endpoint = null;
        for (final String param : sParams) {
            if (param.startsWith(USER_PREFIX)) {
                user = param.replace(USER_PREFIX, "");
                if (StringUtils.isNotBlank(user)) {
                    user = Framework.getProperty(user, user);
                }
            } else if (param.startsWith(PASSWORD_PREFIX)) {
                password = param.replace(PASSWORD_PREFIX, "");
                if (StringUtils.isNotBlank(password)) {
                    password = Framework.getProperty(password, password);
                }
            } else if (param.startsWith(ENDPOINT_PREFIX)) {
                endpoint = param.replace(ENDPOINT_PREFIX, "");
                if (StringUtils.isNotBlank(endpoint)) {
                    endpoint = Framework.getProperty(endpoint, endpoint);
                }
            }
        }

        return getWSInfo(endpoint, user, password);

    }

    public static ResultInfo getWSInfo(final String endpoint, final String username, final String password) {

        final ResultInfo resultInfo = new ResultInfo();

        try {
            final URL serviceUrl = new URL(endpoint);

            final HttpClientBuilder builder = HttpClientBuilder.create();
            HttpClientContext context = null;

            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                final CredentialsProvider provider = new BasicCredentialsProvider();
                final AuthScope scope = new AuthScope(serviceUrl.getHost(), serviceUrl.getPort(), AuthScope.ANY_REALM);
                final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
                provider.setCredentials(scope, credentials);
                builder.setDefaultCredentialsProvider(provider);

                final HttpHost targetHost = new HttpHost(serviceUrl.getHost(), serviceUrl.getPort(),
                        serviceUrl.getProtocol());

                final AuthCache authCache = new BasicAuthCache();
                authCache.put(targetHost, new BasicScheme());

                context = HttpClientContext.create();
                context.setCredentialsProvider(provider);
                context.setAuthCache(authCache);

            }
            final HttpClient httpClient = builder.build();

            final HttpGet httpGet = new HttpGet(serviceUrl.toString());

            HttpResponse httpResponse = null;
            if (context == null) {
                httpResponse = httpClient.execute(httpGet);
            } else {
                httpResponse = httpClient.execute(httpGet, context);
            }

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                resultInfo.setStatut(ResultEnum.KO);
            }
            resultInfo.setDescription(String.valueOf(httpResponse.getStatusLine().getStatusCode()));

        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
            resultInfo.setDescription(e.getMessage());
        }

        return resultInfo;
    }

}
