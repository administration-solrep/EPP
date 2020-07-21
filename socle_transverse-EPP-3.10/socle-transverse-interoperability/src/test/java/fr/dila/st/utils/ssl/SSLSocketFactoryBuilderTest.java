package fr.dila.st.utils.ssl;

import fr.dila.st.utils.ssl.SSLSocketFactoryBuilder;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import junit.framework.Assert;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import fr.dila.st.rest.helper.HttpClientBuilder;

public class SSLSocketFactoryBuilderTest {

	private static final String	DICTAO_KEYSTORE_FILE		= "keystore_client.p12";
	private static final String	DICTAO_KEYSTORE_PASS		= "changeme";
	private static final String	DICTAO_KEYSTORE_FORMAT		= "PKCS12";

	private static final String	DICTAO_TRUSTSTORE_FILE		= "truststore_client.jks";
	private static final String	DICTAO_TRUSTSTORE_PASS		= "secret";
	private static final String	DICTAO_TRUSTSTORE_FORMAT	= "JKS";

	@Test
	public void testSSLSocketFactoryBuilder() {

		Properties systemProps = System.getProperties();

		systemProps.put("javax.net.debug", "ssl,handshake");

		systemProps.put("javax.net.ssl.keyStore", DICTAO_KEYSTORE_FILE);
		systemProps.put("javax.net.ssl.keyStorePassword", DICTAO_KEYSTORE_PASS);
		systemProps.put("javax.net.ssl.keyStoreType", DICTAO_KEYSTORE_FORMAT);

		systemProps.put("javax.net.ssl.trustStore", DICTAO_TRUSTSTORE_FILE);
		systemProps.put("javax.net.ssl.trustStorePassword", DICTAO_TRUSTSTORE_PASS);
		systemProps.put("javax.net.ssl.trustStoreType", DICTAO_TRUSTSTORE_FORMAT);

		SSLSocketFactoryBuilder sslfg = new SSLSocketFactoryBuilder("client1");
		Assert.assertNotNull(sslfg);
		Assert.assertEquals(DICTAO_KEYSTORE_FILE, sslfg.getKeystore());
		Assert.assertEquals(DICTAO_KEYSTORE_PASS, sslfg.getKeystorePassword());
		Assert.assertEquals(DICTAO_KEYSTORE_FORMAT, sslfg.getKeystoreType());

		Assert.assertEquals(DICTAO_TRUSTSTORE_FILE, sslfg.getTruststore());
		Assert.assertEquals(DICTAO_TRUSTSTORE_PASS, sslfg.getTruststorePassword());
		Assert.assertEquals(DICTAO_TRUSTSTORE_FORMAT, sslfg.getTruststoreType());

	}

	// Suite aux travaux d'homologation on teste que l'application utilise bien la conf des trustores java
	// Pour faire fonctionner ce test , il faut faire tourner un apache en local, créer un certificat, l'ajouter au
	// trustore spécifié...
	// ABI
	// @Test
	// public void testApacheCertificate() throws IOException, GeneralSecurityException {
	//
	// Properties systemProps = System.getProperties();
	// systemProps.put("javax.net.debug", "ssl,handshake");
	// systemProps.put("javax.net.ssl.keyStore", true);
	// systemProps.put("javax.net.ssl.trustStore", "/platform/opt/jdk1.6/jre/lib/security/cacerts");
	//
	// try {
	// URL myURL = new URL("https://solonepg.fr");
	// HttpClientBuilder httpclientBuilder = new HttpClientBuilder(myURL);
	// HttpClient hc = httpclientBuilder.build();
	// hc.execute(new HttpGet());
	// Assert.assertTrue(false);
	// } catch (Exception e) {
	// Assert.assertTrue(true);
	// }
	//
	// systemProps.put("javax.net.ssl.trustStore", "/platform/home/user/workspace/cacerts");
	// systemProps.put("javax.net.ssl.trustStorePassword", "changeit");
	// systemProps.put("javax.net.ssl.trustStoreType", DICTAO_TRUSTSTORE_FORMAT);
	//
	// try {
	// URL myURL = new URL("https", "solonepg.fr", 443, "");
	// HttpClientBuilder httpclientBuilder = new HttpClientBuilder(myURL);
	// HttpClient hc = httpclientBuilder.build();
	// hc.execute(new HttpGet("https://solonepg.fr"));
	// Assert.assertTrue(true);
	// } catch (Exception e) {
	// Assert.assertTrue(false);
	// }
	// }
}
