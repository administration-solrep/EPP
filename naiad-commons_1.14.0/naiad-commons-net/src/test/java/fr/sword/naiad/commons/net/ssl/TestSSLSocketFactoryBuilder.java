package fr.sword.naiad.commons.net.ssl;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class TestSSLSocketFactoryBuilder {

	
	private static final String DICTAO_KEYSTORE_FILE = "keystore_client.p12";
	private static final String DICTAO_KEYSTORE_PASS = "changeme";
	private static final String DICTAO_KEYSTORE_FORMAT = "PKCS12";
	
	private static final String DICTAO_TRUSTSTORE_FILE = "truststore_client.jks";
	private static final String DICTAO_TRUSTSTORE_PASS= "secret";
	private static final String DICTAO_TRUSTSTORE_FORMAT= "JKS";

	
	
	@Test
	public void testSSLSocketFactoryBuilder() {
		
		Properties systemProps = System.getProperties();
		
		systemProps.put("javax.net.debug", "ssl,handshake");

		systemProps.put("javax.net.ssl.keyStore",			DICTAO_KEYSTORE_FILE);
		systemProps.put("javax.net.ssl.keyStorePassword",	DICTAO_KEYSTORE_PASS);
		systemProps.put("javax.net.ssl.keyStoreType",		DICTAO_KEYSTORE_FORMAT);

		systemProps.put("javax.net.ssl.trustStore",			DICTAO_TRUSTSTORE_FILE);
		systemProps.put("javax.net.ssl.trustStorePassword",	DICTAO_TRUSTSTORE_PASS);
		systemProps.put("javax.net.ssl.trustStoreType",		DICTAO_TRUSTSTORE_FORMAT);

		SSLSocketFactoryBuilder sslfg = new SSLSocketFactoryBuilder("client1");
		Assert.assertNotNull(sslfg);
		Assert.assertEquals(DICTAO_KEYSTORE_FILE, sslfg.getKeystore());
		Assert.assertEquals(DICTAO_KEYSTORE_PASS, sslfg.getKeystorePassword());
		Assert.assertEquals(DICTAO_KEYSTORE_FORMAT, sslfg.getKeystoreType());
		
		Assert.assertEquals(DICTAO_TRUSTSTORE_FILE, sslfg.getTruststore());
		Assert.assertEquals(DICTAO_TRUSTSTORE_PASS, sslfg.getTruststorePassword());
		Assert.assertEquals(DICTAO_TRUSTSTORE_FORMAT, sslfg.getTruststoreType());

	}
	
}
