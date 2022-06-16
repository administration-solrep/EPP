package fr.dila.st.utils.ssl;

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

import org.apache.commons.lang3.StringUtils;

/**
 * SSLFactory generator
 * 
 * @author fbarmes
 * 
 */
public class SSLSocketFactoryBuilder {

    private static final String DEFAULT_PASSWORD = "secret";
    public static final String DEFAULT_TYPE = "JKS";

    private String keyAlias = null;

    private String keystore = null;
    private String keystoreType = DEFAULT_TYPE;

    private String truststore = null;
    private String truststoreType = DEFAULT_TYPE;

    /**
     * Default constructor
     */
    public SSLSocketFactoryBuilder() {
        this(null);
    }

    /**
     * Default constructor, uses the values specified in the default javax.net.ssl
     * system properties
     * 
     * @param keyAlias the keystore key alias to use
     */
    public SSLSocketFactoryBuilder(String keyAlias) {
        this(keyAlias, System.getProperty("javax.net.ssl.keyStore"), System.getProperty("javax.net.ssl.keyStoreType"),
                System.getProperty("javax.net.ssl.trustStore"), System.getProperty("javax.net.ssl.trustStoreType"));
    }

    /**
     * Constructor setting only the files
     * 
     * @param keyAlias
     * @param keystore
     * @param truststore
     */
    public SSLSocketFactoryBuilder(String keyAlias, String keystore, String truststore) {
        this(keyAlias, keystore, DEFAULT_TYPE, truststore, DEFAULT_TYPE);
    }

    public static String getKeystorePwd() {
        return StringUtils.defaultIfEmpty(System.getProperty("javax.net.ssl.keyStorePassword"), DEFAULT_PASSWORD);

    }

    public static String getTruststorePwd() {
        return StringUtils.defaultIfEmpty(System.getProperty("javax.net.ssl.trustStorePassword"), DEFAULT_PASSWORD);
    }

    /**
     * Complete constructor
     * 
     * @param keyAlias       the key alias to use in the generated SSL
     *                       communications
     * @param keystore       the keystore file
     * @param keystoreType   the truststore type
     * @param truststore     the truststore file
     * @param truststoreType the truststore type
     */
    public SSLSocketFactoryBuilder(String keyAlias, String keystore, String keystoreType, String truststore,
            String truststoreType) {
        super();

        this.keyAlias = keyAlias;
        this.keystore = keystore;
        this.keystoreType = keystoreType;
        this.truststore = truststore;
        this.truststoreType = truststoreType;
    }

    /**
     * Get the SSLSocketFactory associated with this generator
     * 
     * @param protocol
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public SSLSocketFactory getSSLSocketFactory(String protocol, String keystorePwd, String truststorePwd)
            throws IOException, GeneralSecurityException {

        KeyManager[] keyManagers = null;
        TrustManager[] trustManagers = getTrustManagers(truststorePwd);

        if (this.keyAlias != null) {
            keyManagers = getKeyManagers(keystorePwd);

            // --- override the X509KeyManager implementation
            for (int i = 0; i <= keyManagers.length - 1; i++) {
                if (keyManagers[i] instanceof X509KeyManager) {
                    keyManagers[i] = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], this.keyAlias);
                }
            }
        }

        SSLContext context = SSLContext.getInstance(protocol);
        context.init(keyManagers, trustManagers, null);
        return context.getSocketFactory();
    }

    public org.apache.http.conn.ssl.SSLSocketFactory getApacheSSLSocketFactory(String protocol, String keystorePwd,
            String truststorePwd) throws IOException, GeneralSecurityException {

        KeyManager[] keyManagers = null;

        TrustManager[] trustManagers = getTrustManagers(truststorePwd);

        if (this.keyAlias != null) {
            keyManagers = getKeyManagers(keystorePwd);
            // --- override the X509KeyManager implementation
            for (int i = 0; i <= keyManagers.length - 1; i++) {
                if (keyManagers[i] instanceof X509KeyManager) {
                    keyManagers[i] = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], this.keyAlias);
                }
            }
        }
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(keyManagers, trustManagers, null);
        org.apache.http.conn.ssl.SSLSocketFactory ssf = new org.apache.http.conn.ssl.SSLSocketFactory(context);

        return ssf;

    }

    /**
     * Get the list of KeyManagers from the specified keystore
     * 
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private KeyManager[] getKeyManagers(String keystorePwd) throws IOException, GeneralSecurityException {
        if (getKeystore() == null) {
            throw new IOException("No keystore defined !");
        }

        try (FileInputStream fis = new FileInputStream(getKeystore())) {
            KeyStore kStore = KeyStore.getInstance(getKeystoreType());
            kStore.load(fis, keystorePwd.toCharArray());

            // Init a key store with the given file.
            String alg = KeyManagerFactory.getDefaultAlgorithm();
            // Init the key manager factory with the loaded key store
            KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);
            kmFact.init(kStore, keystorePwd.toCharArray());

            return kmFact.getKeyManagers();
        }
    }

    /**
     * Get the list of trustManagers associated with the given truststore
     * 
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private TrustManager[] getTrustManagers(String truststorePwd) throws IOException, GeneralSecurityException {

        String alg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getTruststore());

            KeyStore kStore = KeyStore.getInstance(getTruststoreType());
            kStore.load(fis, truststorePwd.toCharArray());

            tmFact.init(kStore);

            return tmFact.getTrustManagers();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
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

    public final String getTruststoreType() {
        return truststoreType;
    }

    public final void setTruststoreType(String truststoreType) {
        this.truststoreType = truststoreType;
    }

}
