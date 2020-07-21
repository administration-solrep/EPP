package fr.dila.st.rest.helper;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import fr.dila.st.utils.ssl.SSLSocketFactoryBuilder;

/**
 * Build an Apache HTTP Builder according to various parameters
 * 
 * @author fbarmes
 * 
 */
public class HttpClientBuilder {

	private URL		url			= null;

	// private boolean isSSL = false;
	// private boolean hasCredentials = false;

	private String	keyAlias	= null;

	// private String username = null;
	// private String password = null;

	/**
	 * Default constructor for a client with no special features
	 */
	public HttpClientBuilder(URL url) {
		super();
		this.url = url;
	}

	// public void setSSL() {
	// this.setSSL(true);
	// }

	// public void setCredentials(String username, String password) {
	// this.setHasCredentials(true);
	// this.setUsername(username);
	// this.setPassword(password);
	// }

	public HttpClient build() throws GeneralSecurityException, IOException {

		DefaultHttpClient httpClient = null;
		boolean isHttps = url.getProtocol().equalsIgnoreCase("https");

		if (isHttps) {
			httpClient = buildSSLClient();
		} else {
			httpClient = new DefaultHttpClient();
		}

		// if(this.hasCredentials()) {
		// httpClient.getCredentialsProvider().setCredentials(
		// new AuthScope(this.url.getHost(), this.url.getPort()),
		// new UsernamePasswordCredentials(this.getUsername(), this.getPassword()));
		// }

		return httpClient;
	}

	private DefaultHttpClient buildSSLClient() throws GeneralSecurityException, IOException {

		SSLSocketFactoryBuilder sslfBuilder = new SSLSocketFactoryBuilder(this.getKeyAlias());
		SSLSocketFactory ssf = sslfBuilder.getApacheSSLSocketFactory("TLS");

		ssf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

		Scheme httpsScheme = new Scheme("https", ssf, url.getPort());
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);

		HttpParams params = new BasicHttpParams();
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);

		DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);

		return httpClient;
	}

	// private boolean hasCredentials() {
	// return hasCredentials;
	// }
	//
	//
	// private void setHasCredentials(boolean hasCredentials) {
	// this.hasCredentials = hasCredentials;
	// }
	//
	//
	// private String getUsername() {
	// return username;
	// }
	//
	//
	// private void setUsername(String username) {
	// this.username = username;
	// }
	//
	//
	// private String getPassword() {
	// return password;
	// }
	//
	//
	// private void setPassword(String password) {
	// this.password = password;
	// }

	public final String getKeyAlias() {
		return keyAlias;
	}

	public final void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}

}
