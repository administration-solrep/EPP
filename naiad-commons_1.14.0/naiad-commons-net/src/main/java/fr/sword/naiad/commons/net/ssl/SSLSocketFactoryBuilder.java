package fr.sword.naiad.commons.net.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;


/**
 * SSLFactory generator
 * @author fbarmes
 *
 */
public class SSLSocketFactoryBuilder {

	public static final String DEFAULT_PASSWORD = "secret";
	public static final String DEFAULT_TYPE = "JKS";
	
	private String keyAlias = null;
	
	private String keystore = null;
	private String keystorePassword = DEFAULT_PASSWORD;
	private String keystoreType = DEFAULT_TYPE;
	
	private String truststore = null;
	private String truststorePassword = DEFAULT_PASSWORD;
	private String truststoreType = DEFAULT_TYPE;
	
	
	/**
	 * Default constructor
	 */
	public SSLSocketFactoryBuilder() {
		this(null);
	}
	
	/**
	 * Default constructor, uses the values specified in the default javax.net.ssl system properties
	 * @param keyAlias the keystore key alias to use 
	 */
	public SSLSocketFactoryBuilder(String keyAlias) {		
		this(keyAlias,
			System.getProperty("javax.net.ssl.keyStore"),
			System.getProperty("javax.net.ssl.keyStorePassword"),
			System.getProperty("javax.net.ssl.keyStoreType"),
			System.getProperty("javax.net.ssl.trustStore"),
			System.getProperty("javax.net.ssl.trustStorePassword"),
			System.getProperty("javax.net.ssl.trustStoreType"));
	}
	
	/**
	 * Constructor setting only the files
	 * @param keyAlias
	 * @param keystore
	 * @param truststore
	 */
	public SSLSocketFactoryBuilder(String keyAlias, String keystore, String truststore) {
		this(keyAlias, keystore, DEFAULT_PASSWORD, DEFAULT_TYPE, truststore, DEFAULT_PASSWORD, DEFAULT_TYPE);
	}

	/**
	 * Complete constructor
	 * @param keyAlias the key alias to use in the generated SSL communications 
	 * @param keystore the keystore file
	 * @param keystorePassword the keystore password
	 * @param keystoreType the truststore type
	 * @param truststore the truststore file
	 * @param truststorePassword the truststore password
	 * @param truststoreType the truststore type
	 */
	public SSLSocketFactoryBuilder(String keyAlias, 
			String keystore, String keystorePassword, String keystoreType, 
			String truststore, String truststorePassword, String truststoreType) {
		super();
		
		this.keyAlias = keyAlias;
		this.keystore = keystore;
		this.keystorePassword = keystorePassword;
		this.keystoreType = keystoreType;
		this.truststore = truststore;
		this.truststorePassword = truststorePassword;
		this.truststoreType = truststoreType;
	}

	
	/**
	 * Get the SSLSocketFactory associated with this generator
	 * @param protocol
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public SSLSocketFactory getSSLSocketFactory(String protocol) throws IOException, GeneralSecurityException {

		KeyManager[] keyManagers = null;
		TrustManager[] trustManagers = getTrustManagers();

		if(this.keyAlias != null) {
			keyManagers = getKeyManagers();
			
			//--- override the X509KeyManager implementation
			for (int i = 0; i <= keyManagers.length-1; i++) {
				if (keyManagers[i] instanceof X509KeyManager) {
					keyManagers[i] = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], this.keyAlias);
				}
			}			
		}
		//overriding trustManagers
//		for (int i = 0; i <= trustManagers.length-1 ; i++) {
//		    trustManagers[i] = new NoopTrustManager();    
//		}
//		

		SSLContext context = SSLContext.getInstance(protocol);
		context.init(keyManagers, trustManagers, null);
		SSLSocketFactory ssf = context.getSocketFactory();
		
		return ssf;
	}
	
	
	public org.apache.http.conn.ssl.SSLSocketFactory getApacheSSLSocketFactory(String protocol) throws IOException, GeneralSecurityException {

		KeyManager[] keyManagers = null; 
		
		TrustManager[] trustManagers = getTrustManagers();

		if(this.keyAlias != null) {
			keyManagers = getKeyManagers();
			//--- override the X509KeyManager implementation
			for (int i = 0; i <= keyManagers.length-1; i++) {
				if (keyManagers[i] instanceof X509KeyManager) {
					keyManagers[i] = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], this.keyAlias);
				}
			}			
		}
		//overriding trustManagers
		for (int i = 0; i <= trustManagers.length-1 ; i++) {
			trustManagers[i] = new NoopTrustManager();                          
		}

		SSLContext context = SSLContext.getInstance(protocol);
		context.init(keyManagers, trustManagers, null);
		org.apache.http.conn.ssl.SSLSocketFactory ssf = new org.apache.http.conn.ssl.SSLSocketFactory(context);
		
		
		return ssf;
		
	}
	
	/**
	 * Get the list of KeyManagers from the specified keystore
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException {

		// Init a key store with the given file.
		String alg = KeyManagerFactory.getDefaultAlgorithm();
		KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);

		if(getKeystore() == null) {
			throw new IOException("No keystore defined : " + getKeystore());
		}
		
		FileInputStream fis = new FileInputStream(getKeystore());
				
		KeyStore keyStore = KeyStore.getInstance(getKeystoreType());
		keyStore.load(fis, getKeystorePassword().toCharArray());
		fis.close();

		// Init the key manager factory with the loaded key store
		kmFact.init(keyStore, getKeystorePassword().toCharArray());

		return kmFact.getKeyManagers();
	}

	/**
	 * Get the list of trustManagers associated with the given truststore
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException {

		String alg = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

		FileInputStream fis = new FileInputStream(getTruststore());
		KeyStore keystore = KeyStore.getInstance(getTruststoreType());
		keystore.load(fis, getTruststorePassword().toCharArray());
		fis.close();

		tmFact.init(keystore);

		return tmFact.getTrustManagers();
	}

	
	public final String getAlias() {
		return keyAlias;
	}

	public final void setAlias(String alias) {
		this.keyAlias = alias;
	}

	public final String getKeystore() {
		return keystore;
	}

	public final void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public final String getKeystorePassword() {
		return keystorePassword;
	}

	public final void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public final String getKeystoreType() {
		return keystoreType;
	}

	public final void setKeystoreType(String keystoreType) {
		this.keystoreType = keystoreType;
	}

	public final String getTruststore() {
		return truststore;
	}

	public final void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	public final String getTruststorePassword() {
		return truststorePassword;
	}

	public final void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	public final String getTruststoreType() {
		return truststoreType;
	}

	public final void setTruststoreType(String truststoreType) {
		this.truststoreType = truststoreType;
	}

		
}
