package fr.sword.naiad.commons.net.http.client;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import fr.sword.naiad.commons.net.ssl.NoopX509HostnameVerifier;
import fr.sword.naiad.commons.net.ssl.SSLSocketFactoryBuilder;

/**
 * Build an Apache HTTP Builder according to various parameters
 * @author fbarmes
 *
 */
public class HttpClientBuilder {

    private URL url = null;
    private boolean credentials = false;
    private String keyAlias = null;
    private String username = null;
    private String password = null;

    /**
     * Default constructor for a client with no special features
     */
    public HttpClientBuilder(final URL url) {
        super();
        this.url = url;
    }

    public void setCredentials(final String username, final String password) {
        this.setHasCredentials(true);
        this.setUsername(username);
        this.setPassword(password);
    }

    public HttpClient build() throws GeneralSecurityException, IOException {

        DefaultHttpClient httpClient = null;
        final boolean isHttps = url.getProtocol().equalsIgnoreCase("https");

        if (isHttps) {
            httpClient = buildSSLClient();
        } else {
            httpClient = new DefaultHttpClient();
        }

        if (this.hasCredentials()) {
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(this.url.getHost(), this.url.getPort()),
                    new UsernamePasswordCredentials(this.getUsername(), this.getPassword()));
        }

        return httpClient;
    }

    private DefaultHttpClient buildSSLClient() throws GeneralSecurityException, IOException {

        final SSLSocketFactoryBuilder sslfBuilder = new SSLSocketFactoryBuilder(this.getKeyAlias());
        final SSLSocketFactory ssf = sslfBuilder.getApacheSSLSocketFactory("TLS");

        ssf.setHostnameVerifier(new NoopX509HostnameVerifier());

        // set default port to 443 for https
        int port = 443;
        if (url.getPort() != -1) {
            port = url.getPort();
        }

        final Scheme httpsScheme = new Scheme("https", ssf, port);
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        final HttpParams params = new BasicHttpParams();
        final ClientConnectionManager ccm = new SingleClientConnManager(params, schemeRegistry);

        final DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);

        return httpClient;
    }

    private boolean hasCredentials() {
        return credentials;
    }

    private void setHasCredentials(final boolean hasCredentials) {
        this.credentials = hasCredentials;
    }

    private String getUsername() {
        return username;
    }

    private void setUsername(final String username) {
        this.username = username;
    }

    private String getPassword() {
        return password;
    }

    private void setPassword(final String password) {
        this.password = password;
    }

    public final String getKeyAlias() {
        return keyAlias;
    }

    public final void setKeyAlias(final String keyAlias) {
        this.keyAlias = keyAlias;
    }

}
